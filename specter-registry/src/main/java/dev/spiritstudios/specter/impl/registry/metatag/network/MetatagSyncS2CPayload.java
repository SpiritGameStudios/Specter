package dev.spiritstudios.specter.impl.registry.metatag.network;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@ApiStatus.Internal
public record MetatagSyncS2CPayload<V>(MetatagPair<V> metatagPair) implements CustomPayload {
	public static final Id<MetatagSyncS2CPayload<Object>> ID = new Id<>(Identifier.of(SpecterGlobals.MODID, "metatag_sync"));

	private static final Map<Identifier, CacheEntry<?>> CACHE = new Object2ReferenceOpenHashMap<>();

	@SuppressWarnings("unchecked")
	public static PacketCodec<RegistryByteBuf, MetatagSyncS2CPayload<Object>> CODEC =
		PacketCodec.tuple(
			Identifier.PACKET_CODEC.xmap(
					id -> (Registry<Object>) Registries.ROOT.get(id),
					registry -> registry.getKey().getValue()
				).<RegistryByteBuf>cast()
				.dispatch(
					Metatag::getRegistry,
					registry -> Identifier.PACKET_CODEC.xmap(
						id -> (Metatag<Object, Object>) MetatagHolder.of(registry).specter$getMetatag(id),
						Metatag::getId
					).cast()
				)
				.dispatch(
					entry -> (Metatag<Object, Object>) entry.metatag,
					MetatagPair::packetCodec
				),
			MetatagSyncS2CPayload::metatagPair,
			MetatagSyncS2CPayload::new
		);

	private static void fillCache() {
		if (!CACHE.isEmpty()) return;

		for (Registry<?> registry : Registries.ROOT) {
			MetatagHolder<?> metatagHolder = MetatagHolder.of(registry);
			metatagHolder.specter$getMetatags().forEach(entry -> {
				if (entry.getValue().getSide() == ResourceType.CLIENT_RESOURCES)
					return;

				SpecterGlobals.debug("Caching metatag %s".formatted(entry.getKey()));
				cacheMetatag(entry.getValue());
			});
		}
	}

	private static <R, V> void cacheMetatag(Metatag<R, V> metatag) {
		Map<String, Set<MetatagSyncEntry<V>>> encodedEntries = new Object2ObjectOpenHashMap<>();

		for (Metatag.Entry<R, V> entry : metatag) {
			Identifier id = metatag.getRegistry().getId(entry.key());
			if (id == null)
				throw new IllegalStateException("Registry entry " + entry.key() + " has no identifier");

			encodedEntries.computeIfAbsent(id.getNamespace(), identifier -> new HashSet<>()).add(new MetatagSyncEntry<>(id.getPath(), entry.value()));
		}

		Set<MetatagPair<V>> metatagPairs = new HashSet<>();
		for (Map.Entry<String, Set<MetatagSyncEntry<V>>> entry : encodedEntries.entrySet())
			metatagPairs.add(new MetatagPair<>(entry.getKey(), entry.getValue(), metatag));

		CACHE.put(metatag.getId(), new CacheEntry<>(metatagPairs));
	}

	public static Stream<MetatagSyncS2CPayload<?>> createPayloads() {
		fillCache();

		return CACHE.values().stream().flatMap(CacheEntry::toPayloads);
	}

	public static void clearCache() {
		CACHE.clear();
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	private record CacheEntry<V>(Set<MetatagPair<V>> metatagPairs) {
		Stream<MetatagSyncS2CPayload<V>> toPayloads() {
			return metatagPairs.stream().map(MetatagSyncS2CPayload::new);
		}
	}

	public record MetatagSyncEntry<V>(String id, V value) {
		public static <V> PacketCodec<RegistryByteBuf, MetatagSyncEntry<V>> packetCodec(Metatag<?, V> metatag) {
			return PacketCodec.tuple(
				PacketCodecs.STRING,
				MetatagSyncEntry::id,
				metatag.getPacketCodec(),
				MetatagSyncEntry::value,
				MetatagSyncEntry::new
			);
		}
	}

	public record MetatagPair<V>(String namespace, Set<MetatagSyncEntry<V>> entries,
								 Metatag<?, V> metatag) {
		public static <V> PacketCodec<RegistryByteBuf, MetatagPair<V>> packetCodec(Metatag<?, V> metatag) {
			return PacketCodec.tuple(
				PacketCodecs.STRING,
				MetatagPair::namespace,
				PacketCodecs.collection(
					HashSet::newHashSet,
					MetatagSyncEntry.packetCodec(metatag),
					Integer.MAX_VALUE
				),
				MetatagPair::entries,
				(namespace, entries) -> new MetatagPair<>(namespace, entries, metatag)
			);
		}
	}
}
