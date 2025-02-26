package dev.spiritstudios.specter.impl.registry.reloadable.network;

import com.google.common.collect.ImmutableList;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ReloadableRegistrySyncS2CPayload(
	Entry<?> entry,
	boolean finished
) implements CustomPayload {
	public static final Id<ReloadableRegistrySyncS2CPayload> ID = new Id<>(Identifier.of(SpecterGlobals.MODID, "reloadable_registry_sync"));
	public static final PacketCodec<RegistryByteBuf, ReloadableRegistrySyncS2CPayload> CODEC = PacketCodec.tuple(
		Entry.PACKET_CODEC,
		ReloadableRegistrySyncS2CPayload::entry,
		PacketCodecs.BOOLEAN,
		ReloadableRegistrySyncS2CPayload::finished,
		ReloadableRegistrySyncS2CPayload::new
	);

	private static @Nullable List<ReloadableRegistrySyncS2CPayload> CACHE;

	public static void clearCache() {
		CACHE = null;
	}

	public static List<ReloadableRegistrySyncS2CPayload> getOrCreatePayloads(MinecraftServer server) {
		if (CACHE != null) return CACHE;
		@NotNull List<ReloadableRegistrySyncS2CPayload> entries = SpecterReloadableRegistriesImpl.syncingCodecs().keySet().stream().map(key -> createEntry(
				key,
				SpecterReloadableRegistries.lookup().orElseThrow().getOrThrow(key)
			))
			.map(entry -> new ReloadableRegistrySyncS2CPayload(entry, false))
			.collect(Collectors.toList());

		if (entries.isEmpty()) {
			CACHE = List.of();
			return CACHE;
		}

		ReloadableRegistrySyncS2CPayload last = entries.getLast();
		entries.removeLast();
		entries.add(new ReloadableRegistrySyncS2CPayload(last.entry(), true));

		CACHE = ImmutableList.copyOf(entries);
		return CACHE;
	}

	private static <T> ReloadableRegistrySyncS2CPayload.Entry<T> createEntry(RegistryKey<Registry<T>> key, RegistryWrapper<T> registry) {
		return new ReloadableRegistrySyncS2CPayload.Entry<>(
			key,
			registry.streamEntries().collect(Collectors.toMap(
				e -> e.registryKey().getValue(),
				RegistryEntry.Reference::value
			))
		);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public record Entry<T>(RegistryKey<Registry<T>> key, Map<Identifier, T> entries) {
		private static final PacketCodec<ByteBuf, RegistryKey<? extends Registry<?>>> REGISTRY_KEY_CODEC =
			Identifier.PACKET_CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);

		public static final PacketCodec<RegistryByteBuf, Entry<?>> PACKET_CODEC = REGISTRY_KEY_CODEC.<RegistryByteBuf>cast().dispatch(
			Entry::key,
			key -> packetCodec(SpecterReloadableRegistriesImpl.syncingCodecs().get(key))
		);

		@SuppressWarnings("unchecked")
		public static <T> PacketCodec<RegistryByteBuf, Entry<T>> packetCodec(PacketCodec<RegistryByteBuf, T> entryCodec) {
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
