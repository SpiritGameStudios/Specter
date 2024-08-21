package dev.spiritstudios.specter.impl.config.network;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;

public record ConfigSyncS2CPayload(Map<Identifier, String> configs) implements CustomPayload {
	public static final CustomPayload.Id<ConfigSyncS2CPayload> ID = new CustomPayload.Id<>(Identifier.of(SpecterGlobals.MODID, "config_sync"));
	public static final PacketCodec<ByteBuf, ConfigSyncS2CPayload> CODEC =
		PacketCodec.tuple(
			PacketCodecs.map(Object2ObjectOpenHashMap::new, Identifier.PACKET_CODEC, PacketCodecs.STRING),
			ConfigSyncS2CPayload::configs,
			ConfigSyncS2CPayload::new
		);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ConfigSyncS2CPayload.ID;
	}
}

