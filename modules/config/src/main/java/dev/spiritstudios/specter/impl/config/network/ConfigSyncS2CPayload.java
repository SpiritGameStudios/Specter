package dev.spiritstudios.specter.impl.config.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.impl.config.ConfigHolderRegistry;
import dev.spiritstudios.specter.impl.core.Specter;

public record ConfigSyncS2CPayload(ConfigHolder<?, ?> config) implements CustomPacketPayload {
	public static final Type<ConfigSyncS2CPayload> ID = new Type<>(Specter.id("config_sync"));
	public static final StreamCodec<ByteBuf, ConfigSyncS2CPayload> CODEC = StreamCodec.composite(
			StreamCodec.<ByteBuf, ConfigHolder<?, ?>>ofMember(
					(value, buf) -> {
						ResourceLocation.STREAM_CODEC.encode(buf, value.id());
						value.packetEncode(buf);
					},
					buf -> {
						ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
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

		server.getPlayerList().getPlayers().forEach(
				player -> payloads.forEach(payload -> ServerPlayNetworking.send(player, payload)));
	}

	@Override
	public Type<ConfigSyncS2CPayload> type() {
		return ID;
	}
}
