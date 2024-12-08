package dev.spiritstudios.specter.api.serialization;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec3d;

public final class SpecterPacketCodecs {
	public static final PacketCodec<ByteBuf, Vec3d> VEC3D = PacketCodecs.VECTOR3F.xmap(
		Vec3d::new,
		Vec3d::toVector3f
	);

	private SpecterPacketCodecs() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	public static <T extends Enum<T>> PacketCodec<ByteBuf, T> enumCodec(Class<T> clazz) {
		T[] values = clazz.getEnumConstants();

		return PacketCodecs.VAR_INT.xmap(
			ordinal -> values[ordinal],
			Enum::ordinal
		);
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
