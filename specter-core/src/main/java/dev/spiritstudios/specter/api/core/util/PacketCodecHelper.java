package dev.spiritstudios.specter.api.core.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.codec.PacketCodec;

public final class PacketCodecHelper {
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
