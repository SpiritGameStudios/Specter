package dev.spiritstudios.specter.api.core.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.codec.PacketCodec;

public final class PacketCodecHelper {
	/**
	 * Creates a PacketCodec for a Pair of values
	 *
	 * @param first  The PacketCodec for the first value
	 * @param second The PacketCodec for the second value
	 * @param <B>    The type of the buffer
	 * @param <F>    The type of the first value
	 * @param <S>    The type of the second value
	 * @return The created PacketCodec
	 */
	public static <B, F, S> PacketCodec<B, Pair<F, S>> pair(PacketCodec<? super B, F> first, PacketCodec<? super B, S> second) {
		return PacketCodec.tuple(
			first,
			Pair::getFirst,
			second,
			Pair::getSecond,
			Pair::of
		);
	}
}
