package dev.spiritstudios.specter.impl.config.network;

import static dev.spiritstudios.specter.api.core.SpecterGlobals.MODID;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.config.ConfigHolderRegistry;

public record ConfigSyncS2CPayload(ConfigHolder<?, ?> config) implements CustomPayload {
	public static final Id<ConfigSyncS2CPayload> ID = new Id<>(Identifier.of(MODID, "config_sync"));
	public static final PacketCodec<RegistryByteBuf, ConfigSyncS2CPayload> CODEC = PacketCodec.tuple(
			PacketCodec.of(
					(holder, buf) -> {
						Identifier.PACKET_CODEC.encode(buf, holder.id());
						SpecterGlobals.debug("Encoding config sync packet for %s".formatted(holder.id()));
						encodeConfigValues(buf, holder.get());
					},
					buf -> {
						Identifier id = Identifier.PACKET_CODEC.decode(buf);
						ConfigHolder<?, ?> holder = ConfigHolderRegistry.get(id);

						decodeConfigValues(buf, holder.get());

						return holder;
					}
			),
			ConfigSyncS2CPayload::config,
			ConfigSyncS2CPayload::new
	);

	private static final List<ConfigSyncS2CPayload> CACHE = new ArrayList<>();

	public static void clearCache() {
		CACHE.clear();
	}

	public static List<ConfigSyncS2CPayload> getPayloads() {
		if (CACHE.isEmpty()) CACHE.addAll(ConfigHolderRegistry.createPayloads());
		return CACHE;
	}

	public static void sendPayloadsToAll(MinecraftServer server) {
		List<ConfigSyncS2CPayload> payloads = ConfigSyncS2CPayload.getPayloads();

		server.getPlayerManager().getPlayerList().forEach(
				player -> payloads.forEach(payload -> ServerPlayNetworking.send(player, payload)));
	}

	private static void encodeConfigValues(RegistryByteBuf buffer, Config config) {
		config.values().forEach((key, either) -> {
			either
					.ifLeft(value -> {
						if (value.sync()) encodeField(buffer, value);
					})
					.ifRight(subConfig -> encodeConfigValues(buffer, subConfig));
		});
	}

	private static void decodeConfigValues(RegistryByteBuf buffer, Config config) {
		config.values().forEach((key, either) -> {
			either
					.ifLeft(value -> {
						if (value.sync()) decodeField(buffer, value);
					})
					.ifRight(subConfig -> decodeConfigValues(buffer, subConfig));
		});
	}

	private static <V> void encodeField(RegistryByteBuf buffer, Value<V> value) {
		value.packetCodec().ifPresent(codec ->
				codec.encode(buffer, value.get()));
	}

	private static <V> void decodeField(RegistryByteBuf buffer, Value<V> value) {
		value.packetCodec().ifPresent(codec ->
				value.override(codec.decode(buffer)));
	}

	@Override
	public Id<ConfigSyncS2CPayload> getId() {
		return ID;
	}
}
