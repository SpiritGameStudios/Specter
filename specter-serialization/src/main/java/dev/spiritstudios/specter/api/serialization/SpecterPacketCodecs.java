package dev.spiritstudios.specter.api.serialization;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public final class SpecterPacketCodecs {
	private SpecterPacketCodecs() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	public static <T extends Enum<T>> PacketCodec<ByteBuf, T> enumPacketCodec(Class<T> clazz) {
		T[] values = clazz.getEnumConstants();

		return PacketCodecs.indexed(id -> {
			if (id < 0 || id >= values.length) throw new IllegalArgumentException("Enum ordinal out of bounds: " + id);
			return values[id];
		}, Enum::ordinal);
	}
}
