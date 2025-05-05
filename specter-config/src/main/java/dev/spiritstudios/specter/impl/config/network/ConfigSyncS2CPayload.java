package dev.spiritstudios.specter.impl.config.network;

import static dev.spiritstudios.specter.api.core.SpecterGlobals.MODID;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.config.ConfigHolderRegistry;

public record ConfigSyncS2CPayload(ConfigHolder<?, ?> config) implements CustomPayload {
	public static final Id<ConfigSyncS2CPayload> ID = new Id<>(Identifier.of(MODID, "config_sync"));
	public static final PacketCodec<ByteBuf, ConfigSyncS2CPayload> CODEC = PacketCodec.tuple(
		PacketCodec.<ByteBuf, ConfigHolder<?, ?>>of(
			(value, buf) -> {
				Identifier.PACKET_CODEC.encode(buf, value.id());
				SpecterGlobals.debug("Encoding config sync packet for %s".formatted(value.id()));
				value.packetEncode(buf);
			},
			buf -> {
				Identifier id = Identifier.PACKET_CODEC.decode(buf);
				SpecterGlobals.debug("Decoding config sync packet for %s".formatted(id));
				ConfigHolder<?, ?> config = ConfigHolderRegistry.get(id);
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

	public static List<ConfigSyncS2CPayload> getPayloads() {
		if (CACHE.isEmpty()) CACHE.addAll(ConfigHolderRegistry.createPayloads());
		return CACHE;
	}

	public static void sendPayloadsToAll(MinecraftServer server) {
		List<ConfigSyncS2CPayload> payloads = ConfigSyncS2CPayload.getPayloads();

		server.getPlayerManager().getPlayerList().forEach(
			player -> payloads.forEach(payload -> ServerPlayNetworking.send(player, payload)));
	}

	@Override
	public Id<ConfigSyncS2CPayload> getId() {
		return ID;
	}
}
