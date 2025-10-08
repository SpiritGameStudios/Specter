package dev.spiritstudios.specter.impl.config;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.function.TriFunction;

import net.minecraft.network.codec.PacketCodec;

import dev.spiritstudios.specter.api.config.NumericValue;

public class NumericValueImpl<T extends Number & Comparable<T>> extends ValueImpl<T> implements NumericValue<T> {
	private final T min;
	private final T max;

	private final double step;
	private final TriFunction<T, T, T, T> clamp;

	public NumericValueImpl(T defaultValue, Codec<T> codec, PacketCodec<ByteBuf, T> packetCodec, String comment, boolean sync, T min, T max, double step, TriFunction<T, T, T, T> clamp) {
		super(defaultValue, codec, packetCodec, comment, sync);
		this.min = min;
		this.max = max;
		this.step = step;
		this.clamp = clamp;
	}

	@Override
	public T min() {
		return min;
	}

	@Override
	public T max() {
		return max;
	}

	@Override
	public double step() {
		return step;
	}

	@Override
	public T clamp(T value) {
		return clamp.apply(value, min, max);
	}

	@Override
	public void set(T value) {
		super.set(clamp(value));
	}
}
