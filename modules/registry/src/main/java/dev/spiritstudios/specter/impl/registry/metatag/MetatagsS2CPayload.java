package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;

@ApiStatus.Internal
public record MetatagsS2CPayload(List<MetatagData<?, ?>> metatags) {
	public static final StreamCodec<RegistryFriendlyByteBuf, MetatagsS2CPayload> CODEC = MetatagsS2CPayload.MetatagData.CODEC
			.apply(ByteBufCodecs.list())
			.map(MetatagsS2CPayload::new, MetatagsS2CPayload::metatags);

	private static @Nullable MetatagsS2CPayload CACHE;

	private static <R, V> MetatagData<R, V> createData(Metatag<R, V> metatag) {
		return new MetatagData<>(
				metatag,
				metatag instanceof ExistingCombinedMetatag<R, V> existingCombined ?
						existingCombined.rawValues() :
						metatag.values()
		);
	}

	public static MetatagsS2CPayload getOrCreatePayload(HolderLookup.Provider wrapperLookup) {
		if (CACHE != null) return CACHE;

		List<MetatagData<?, ?>> metatags = new ArrayList<>();

		wrapperLookup.listRegistryKeys().forEach(key -> {
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
		private static final StreamCodec<ByteBuf, Metatag<?, ?>> METATAG_CODEC =
				ResourceLocation.STREAM_CODEC.dispatch(
						metatag -> metatag.registryKey().location(),
						key -> ResourceLocation.STREAM_CODEC.map(
								id -> MetatagHolder.ofAny(ResourceKey.createRegistryKey(key))
										.specter$getMetatag(id),
								Metatag::id
						)
				);

		public static final StreamCodec<RegistryFriendlyByteBuf, MetatagData<?, ?>> CODEC = METATAG_CODEC
				.<RegistryFriendlyByteBuf>cast()
				.dispatch(
						MetatagData::metatag,
						MetatagData::codec
				);

		public static <R, V> StreamCodec<RegistryFriendlyByteBuf, MetatagData<R, V>> codec(Metatag<R, V> metatag) {
			return ByteBufCodecs.map(
					expected -> (Map<R, V>) new Object2ObjectLinkedOpenHashMap<R, V>(expected),
					ByteBufCodecs.registry(metatag.registryKey()),
					metatag.packetCodec()
			).map(
					map -> new MetatagData<>(metatag, map),
					MetatagData::entries
			);
		}
	}
}
