package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;

@ApiStatus.Internal
public record MetatagsS2CPayload(List<MetatagData<?, ?>> metatags) {
	public static final PacketCodec<RegistryByteBuf, MetatagsS2CPayload> CODEC = MetatagsS2CPayload.MetatagData.CODEC
			.collect(PacketCodecs.toList())
			.xmap(MetatagsS2CPayload::new, MetatagsS2CPayload::metatags);

	private static @Nullable MetatagsS2CPayload CACHE;

	private static <R, V> MetatagData<R, V> createData(Metatag<R, V> metatag) {
		return new MetatagData<>(
				metatag,
				metatag instanceof ExistingCombinedMetatag<R, V> existingCombined ?
						existingCombined.rawValues() :
						metatag.values()
		);
	}

	public static MetatagsS2CPayload getOrCreatePayload(RegistryWrapper.WrapperLookup wrapperLookup) {
		if (CACHE != null) return CACHE;

		List<MetatagData<?, ?>> metatags = new ArrayList<>();

		wrapperLookup.streamAllRegistryKeys().forEach(key -> {
			MetatagHolder.ofAny(key).specter$getMetatags().forEach(entry -> {
				if (entry.getValue().packetCodec() == null) return;

				metatags.add(createData(entry.getValue()));
			});
		});

		CACHE = new MetatagsS2CPayload(ImmutableList.copyOf(metatags));
		return CACHE;
	}

	public static void clearCache() {
		CACHE = null;
	}

	public record MetatagData<R, V>(Metatag<R, V> metatag, Map<R, V> entries) {
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
			return PacketCodecs.map(
					expected -> (Map<R, V>) new Object2ObjectLinkedOpenHashMap<R, V>(expected),
					PacketCodecs.registryValue(metatag.registryKey()),
					metatag.packetCodec()
			).xmap(
					map -> new MetatagData<>(metatag, map),
					MetatagData::entries
			);
		}
	}
}
