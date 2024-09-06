package dev.spiritstudios.specter.impl.config.network;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.config.ConfigManager;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static dev.spiritstudios.specter.api.core.SpecterGlobals.MODID;

public record ConfigSyncS2CPayload(Config<?> config) implements CustomPayload {
	public static final Id<ConfigSyncS2CPayload> ID = new Id<>(Identifier.of(MODID, "config_sync"));
	public static final PacketCodec<ByteBuf, ConfigSyncS2CPayload> CODEC = PacketCodec.tuple(
		PacketCodec.of(
			Config::packetEncode,
			buf -> {
				Identifier id = Identifier.PACKET_CODEC.decode(buf);
				SpecterGlobals.debug("Decoding config sync packet for %s".formatted(id));
				Config<?> config = ConfigManager.getConfig(id);
				config.save();
				return config.packetDecode(buf);
			}
		),
		ConfigSyncS2CPayload::config,
		ConfigSyncS2CPayload::new
	);

	private static final List<ConfigSyncS2CPayload> CACHE = new ArrayList<>();

	public static void clearCache() {
		CACHE.clear();
	}

	public static List<ConfigSyncS2CPayload> createPayloads() {
		if (CACHE.isEmpty()) {
			CACHE.addAll(
				ConfigManager.getConfigs().stream()
					.map(ConfigSyncS2CPayload::new)
					.toList()
			);
		}

		return CACHE;
	}

	public static void sendPayloadsToAll(MinecraftServer server) {
		List<ConfigSyncS2CPayload> payloads = ConfigSyncS2CPayload.createPayloads();

		server.getPlayerManager().getPlayerList().forEach(
			player -> payloads.forEach(payload -> ServerPlayNetworking.send(player, payload)));
	}

	@Override
	public Id<ConfigSyncS2CPayload> getId() {
		return ID;
	}
}
