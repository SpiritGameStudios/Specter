package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;

@ApiStatus.Internal
public record MetatagsS2CPayload(List<MetatagData<?, ?>> metatags) {
	public static final PacketCodec<RegistryByteBuf, MetatagsS2CPayload> CODEC = MetatagsS2CPayload.MetatagData.CODEC
			.collect(PacketCodecs.toList())
			.xmap(MetatagsS2CPayload::new, MetatagsS2CPayload::metatags);

	private static @Nullable MetatagsS2CPayload CACHE;

	private static <R, V> MetatagData<R, V> createData(Metatag<R, V> metatag, Registry<R> registry) {
		return new MetatagData<>(
				metatag,
				(metatag instanceof ExistingCombinedMetatag<R, V> existingCombined ?
						existingCombined.rawValues() :
						metatag.values()).entrySet().stream()
						.collect(Collectors.toMap(
								entry -> registry.getEntry(entry.getKey()),
								Map.Entry::getValue
						))
		);
	}

	public static MetatagsS2CPayload getOrCreatePayload(DynamicRegistryManager registryManager) {
		if (CACHE != null) return CACHE;

		List<MetatagData<?, ?>> metatags = new ArrayList<>();

		registryManager.streamAllRegistries().forEach(entry -> {
			addEntry(entry, metatags);
		});

		CACHE = new MetatagsS2CPayload(ImmutableList.copyOf(metatags));
		return CACHE;
	}

	private static <R> void addEntry(
			DynamicRegistryManager.Entry<R> registry,
			List<MetatagData<?, ?>> metatags
	) {
		MetatagHolder.of(registry.key()).specter$getMetatags().forEach(entry -> {
			if (entry.getValue().packetCodec() == null) return;
			metatags.add(createData(entry.getValue(), registry.value()));
		});
	}

	public static void clearCache() {
		CACHE = null;
	}

	public record MetatagData<R, V>(Metatag<R, V> metatag, Map<RegistryEntry<R>, V> entries) {
		private static final PacketCodec<ByteBuf, Metatag<?, ?>> METATAG_CODEC =
				Identifier.PACKET_CODEC.dispatch(
						metatag -> metatag.registryKey().getValue(),
						key -> Identifier.PACKET_CODEC.xmap(
								id -> MetatagHolder.ofAny(RegistryKey.ofRegistry(key))
										.specter$getMetatag(id),
								Metatag::id
						)
				);

		public static final PacketCodec<RegistryByteBuf, MetatagData<?, ?>> CODEC = METATAG_CODEC
				.<RegistryByteBuf>cast()
				.dispatch(
						MetatagData::metatag,
						MetatagData::codec
				);

		public static <R, V> PacketCodec<RegistryByteBuf, MetatagData<R, V>> codec(Metatag<R, V> metatag) {
			return PacketCodecs.<RegistryByteBuf, RegistryEntry<R>, V, Map<RegistryEntry<R>, V>>map(
					Object2ObjectLinkedOpenHashMap::new,
					PacketCodecs.registryEntry(metatag.registryKey()),
					metatag.packetCodec()
			).xmap(
					map -> new MetatagData<>(metatag, map),
					MetatagData::entries
			);
		}
	}
}
