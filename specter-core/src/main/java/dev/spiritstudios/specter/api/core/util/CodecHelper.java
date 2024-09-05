package dev.spiritstudios.specter.api.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public final class CodecHelper {
	public static <T extends Enum<T>> Codec<T> createEnumCodec(Class<T> clazz) {
		return Codec.STRING.comapFlatMap(str -> {
			T value;
			try {
				value = Enum.valueOf(clazz, str);
			} catch (IllegalArgumentException e) {
				return DataResult.error(() -> "Unknown enum value: %s".formatted(str));
			}

			return DataResult.success(value);
		}, Enum::name);
	}

	// TODO: Make this use the enum ordinal instead of the name
	public static <T extends Enum<T>> PacketCodec<ByteBuf, T> createEnumPacketCodec(Class<T> clazz) {
		return PacketCodecs.codec(createEnumCodec(clazz));
	}

	public static Codec<Integer> clampedRangeInt(int min, int max) {
		return Codec.INT.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static Codec<Float> clampedRangeFloat(float min, float max) {
		return Codec.FLOAT.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static Codec<Double> clampedRangeDouble(double min, double max) {
		return Codec.DOUBLE.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	private CodecHelper() {
	}
}
