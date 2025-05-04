package dev.spiritstudios.specter.impl.registry.reloadable;

import com.google.common.collect.ImmutableList;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ReloadableRegistriesS2CPayload(List<RegistryData<?>> registries) {
	public static final PacketCodec<RegistryByteBuf, ReloadableRegistriesS2CPayload> CODEC = RegistryData.CODEC
		.collect(PacketCodecs.toList())
		.xmap(ReloadableRegistriesS2CPayload::new, ReloadableRegistriesS2CPayload::registries);

	private static @Nullable ReloadableRegistriesS2CPayload CACHE = null;

	public static ReloadableRegistriesS2CPayload getOrCreatePayload() {
		if (CACHE != null) return CACHE;
		List<RegistryData<?>> entries = SpecterReloadableRegistriesImpl.reloadableRegistries()
			.keySet()
			.stream()
			.map(key -> {
				RegistryKey<Registry<Object>> registryKey = RegistryKey.ofRegistry(key);

				return createData(
					registryKey,
					SpecterReloadableRegistries.lookup()
						.orElseThrow()
						.getOrThrow(registryKey)
				);
			})
			.collect(Collectors.toList());

		if (entries.isEmpty()) {
			CACHE = new ReloadableRegistriesS2CPayload(Collections.emptyList());
			return CACHE;
		}

		CACHE = new ReloadableRegistriesS2CPayload(ImmutableList.copyOf(entries));
		return CACHE;
	}

	private static <T> RegistryData<T> createData(RegistryKey<Registry<T>> key, RegistryWrapper<T> registry) {
		return new RegistryData<>(
			key.getValue(),
			registry.streamEntries().collect(Collectors.<RegistryEntry.Reference<T>, Identifier, T>toMap(
				e -> e.registryKey().getValue(),
				RegistryEntry.Reference::value
			))
		);
	}

	public static void clearCache() {
		CACHE = null;
	}

	public record RegistryData<T>(Identifier key, Map<Identifier, T> entries) {
		public static final PacketCodec<RegistryByteBuf, RegistryData<?>> CODEC = Identifier.PACKET_CODEC
			.<RegistryByteBuf>cast().dispatch(
				RegistryData::key,
				key ->
					codec(SpecterReloadableRegistriesImpl.reloadableRegistries().get(key).packetCodec())
			);

		public static <T> PacketCodec<RegistryByteBuf, RegistryData<T>> codec(PacketCodec<? super RegistryByteBuf, T> entryCodec) {
			return PacketCodec.tuple(
				Identifier.PACKET_CODEC,
				RegistryData::key,
				PacketCodecs.map(
					Object2ObjectLinkedOpenHashMap::new,
					Identifier.PACKET_CODEC,
					entryCodec
				),
				RegistryData::entries,
				RegistryData::new
			);
		}
	}
}
