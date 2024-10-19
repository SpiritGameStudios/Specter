package dev.spiritstudios.specter.api.serialization;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public final class SpecterPacketCodecs {
	private SpecterPacketCodecs() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	public static <T extends Enum<T>> PacketCodec<ByteBuf, T> enumCodec(Class<T> clazz) {
		T[] values = clazz.getEnumConstants();

		return PacketCodecs.indexed(id -> {
			if (id < 0 || id >= values.length) throw new IllegalArgumentException("Enum ordinal out of bounds: " + id);
			return values[id];
		}, Enum::ordinal);
	}

	public static <F, S> PacketCodec<ByteBuf, Pair<F, S>> pair(PacketCodec<ByteBuf, F> first, PacketCodec<ByteBuf, S> second) {
		return PacketCodec.tuple(
			first,
			Pair::getFirst,
			second,
			Pair::getSecond,
			Pair::of
		);
	}
}
