package dev.spiritstudios.specter.api.render.shake;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import dev.spiritstudios.specter.impl.core.Specter;

public record ScreenshakeS2CPayload(double duration, double posIntensity,
									double rotationIntensity) implements CustomPayload {
	public static final ScreenshakeS2CPayload NONE = new ScreenshakeS2CPayload(0, 0, 0);

	public static final CustomPayload.Id<ScreenshakeS2CPayload> ID = new CustomPayload.Id<>(Specter.id("screenshake"));
	public static final PacketCodec<ByteBuf, ScreenshakeS2CPayload> CODEC =
			PacketCodec.tuple(
					PacketCodecs.DOUBLE,
					ScreenshakeS2CPayload::duration,
					PacketCodecs.DOUBLE,
					ScreenshakeS2CPayload::posIntensity,
					PacketCodecs.DOUBLE,
					ScreenshakeS2CPayload::rotationIntensity,
					ScreenshakeS2CPayload::new
			);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
