package dev.spiritstudios.specter.api.render.shake;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import dev.spiritstudios.specter.impl.core.Specter;

public record ScreenshakeS2CPayload(float duration, float posIntensity,
									float rotationIntensity) implements CustomPacketPayload {
	public static final ScreenshakeS2CPayload NONE = new ScreenshakeS2CPayload(0, 0, 0);

	public static final CustomPacketPayload.Type<ScreenshakeS2CPayload> ID = new CustomPacketPayload.Type<>(Specter.id("screenshake"));
	public static final StreamCodec<ByteBuf, ScreenshakeS2CPayload> CODEC =
			StreamCodec.composite(
					ByteBufCodecs.FLOAT, ScreenshakeS2CPayload::duration,
					ByteBufCodecs.FLOAT, ScreenshakeS2CPayload::posIntensity,
					ByteBufCodecs.FLOAT, ScreenshakeS2CPayload::rotationIntensity,
					ScreenshakeS2CPayload::new
			);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
