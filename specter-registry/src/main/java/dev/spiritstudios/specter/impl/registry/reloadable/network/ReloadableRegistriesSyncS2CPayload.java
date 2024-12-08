package dev.spiritstudios.specter.impl.registry.reloadable.network;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ReloadableRegistriesSyncS2CPayload(
	List<Entry<?>> entries
) implements CustomPayload {
	public static final Id<ReloadableRegistriesSyncS2CPayload> ID = new Id<>(Identifier.of(SpecterGlobals.MODID, "reloadable_registry_sync"));
	public static final PacketCodec<PacketByteBuf, ReloadableRegistriesSyncS2CPayload> CODEC = PacketCodec.tuple(
		Entry.PACKET_CODEC.collect(PacketCodecs.toList()),
		ReloadableRegistriesSyncS2CPayload::entries,
		ReloadableRegistriesSyncS2CPayload::new
	);

	private static ReloadableRegistriesSyncS2CPayload CACHED;

	public static void clearCache() {
		CACHED = null;
	}

	public static ReloadableRegistriesSyncS2CPayload get(MinecraftServer server) {
		if (CACHED != null) return CACHED;
		List<ReloadableRegistriesSyncS2CPayload.Entry<?>> entries = SpecterReloadableRegistriesImpl.syncingCodecs().keySet().stream().map(key -> createEntry(
			key,
			server.getReloadableRegistries().getRegistryManager().get(key)
		)).collect(Collectors.toList());
		CACHED = new ReloadableRegistriesSyncS2CPayload(entries);
		return CACHED;
	}

	private static <T> ReloadableRegistriesSyncS2CPayload.Entry<T> createEntry(RegistryKey<Registry<T>> key, Registry<T> registry) {
		return new ReloadableRegistriesSyncS2CPayload.Entry<>(
			key,
			registry.getEntrySet()
				.stream().collect(Collectors.toMap(
					e -> e.getKey().getValue(),
					Map.Entry::getValue
				))
		);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public record Entry<T>(RegistryKey<Registry<T>> key, Map<Identifier, T> entries) {
		private static final PacketCodec<ByteBuf, RegistryKey<? extends Registry<?>>> REGISTRY_KEY_CODEC = Identifier.PACKET_CODEC
			.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);

		public static final PacketCodec<ByteBuf, Entry<?>> PACKET_CODEC = REGISTRY_KEY_CODEC.dispatch(
			Entry::key,
			key -> packetCodec(SpecterReloadableRegistriesImpl.syncingCodecs().get(key))
		);


		@SuppressWarnings("unchecked")
		public static <T> PacketCodec<ByteBuf, Entry<T>> packetCodec(PacketCodec<ByteBuf, T> entryCodec) {
			return PacketCodec.tuple(
				REGISTRY_KEY_CODEC,
				Entry::key,
				PacketCodecs.map(
					Object2ObjectOpenHashMap::new,
					Identifier.PACKET_CODEC,
					entryCodec
				),
				Entry::entries,
				(key, entries) ->
					new Entry<>((RegistryKey<Registry<T>>) key, entries)
			);
		}
	}
}
