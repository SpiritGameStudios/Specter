package dev.spiritstudios.specter.api.core.util;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public final class SpecterPacketCodecs {
	public static final StreamCodec<ByteBuf, Vec3> VEC3D = ByteBufCodecs.VECTOR3F.map(
		Vec3::new,
		Vec3::toVector3f
	);

	private SpecterPacketCodecs() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	public static <T extends Enum<T>> StreamCodec<ByteBuf, T> enumCodec(Class<T> clazz) {
		T[] values = clazz.getEnumConstants();

		return ByteBufCodecs.VAR_INT.map(
			ordinal -> values[ordinal],
			Enum::ordinal
		);
	}

	public static <F, S, B> StreamCodec<B, Pair<F, S>> pair(StreamCodec<B, F> first, StreamCodec<B, S> second) {
		return StreamCodec.composite(
			first,
			Pair::getFirst,
			second,
			Pair::getSecond,
			Pair::of
		);
	}
}
