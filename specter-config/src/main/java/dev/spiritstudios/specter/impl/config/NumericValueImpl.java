package dev.spiritstudios.specter.impl.config;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.config.NumericValue;
import dev.spiritstudios.specter.api.core.math.Range;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class NumericValueImpl<T extends Number & Comparable<T>> extends ValueImpl<T> implements NumericValue<T> {
	private final Range<T> range;
	private final double step;

	public NumericValueImpl(T defaultValue, Codec<T> codec, PacketCodec<ByteBuf, T> packetCodec, String comment, boolean sync, Range<T> range, double step) {
		super(defaultValue, codec, packetCodec, comment, sync);
		this.range = range;
		this.step = step;
	}

	@Override
	public Range<T> range() {
		return range;
	}

	@Override
	public double step() {
		return step;
	}

	@Override
	public void set(T value) {
		super.set(range.clamp(value));
	}
}
