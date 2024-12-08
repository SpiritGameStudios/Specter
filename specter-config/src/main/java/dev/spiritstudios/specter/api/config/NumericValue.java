package dev.spiritstudios.specter.api.config;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.core.math.Range;
import dev.spiritstudios.specter.impl.config.NumericValueImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A config value of a numeric type.
 *
 * @param <T> The type of the numeric value.
 */
public interface NumericValue<T extends Number & Comparable<T>> extends Value<T> {
	Range<T> range();

	double step();

	class Builder<T extends Number & Comparable<T>> {
		protected final T defaultValue;
		protected final Codec<T> codec;
		protected String comment;
		protected boolean sync;
		protected PacketCodec<ByteBuf, T> packetCodec;
		protected Range<T> range;
		protected double step;
		protected BiFunction<T, T, Codec<T>> codecRange;

		public Builder(T defaultValue, Codec<T> codec) {
			this.defaultValue = defaultValue;
			this.codec = codec;
		}

		public NumericValue.Builder<T> codecRange(BiFunction<T, T, Codec<T>> codecRange) {
			this.codecRange = codecRange;
			return this;
		}

		public NumericValue.Builder<T> comment(String comment) {
			this.comment = comment;
			return this;
		}

		public NumericValue.Builder<T> range(T min, T max) {
			this.range = new Range<>(min, max);
			return this;
		}

		public NumericValue.Builder<T> range(Range<T> range) {
			this.range = range;
			return this;
		}

		public NumericValue.Builder<T> step(double step) {
			this.step = step;

			return this;
		}

		public NumericValue.Builder<T> sync() {
			if (packetCodec == null) throw new IllegalStateException("Packet codec must be set to enable syncing");
			this.sync = true;
			return this;
		}

		public NumericValue.Builder<T> packetCodec(PacketCodec<ByteBuf, T> packetCodec) {
			this.packetCodec = packetCodec;
			return this;
		}

		public Value.Builder<List<T>> toList() {
			return new Value.Builder<>(List.of(defaultValue), Codec.list(codec));
		}

		public NumericValue<T> build() {
			Codec<T> rangeCodec = range == null ? codec :
				Optional.ofNullable(codecRange).map(function -> function.apply(range.min(), range.max())).orElse(codec);

			return new NumericValueImpl<>(
				defaultValue,
				rangeCodec,
				packetCodec,
				comment,
				sync,
				range,
				range == null ? 0 : step / (range.max().doubleValue() - range.min().doubleValue())
			);
		}
	}
}
