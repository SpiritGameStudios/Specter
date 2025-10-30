package dev.spiritstudios.specter.api.config;

import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.function.TriFunction;
import dev.spiritstudios.specter.impl.config.NumericValueImpl;

/**
 * A config value of a numeric type.
 *
 * @param <T> The type of the numeric value.
 */
public interface NumericValue<T extends Number & Comparable<T>> extends Value<T> {
	T min();

	T max();

	double step();

	T clamp(T value);

	class Builder<T extends Number & Comparable<T>> {
		protected final T defaultValue;
		protected final Codec<T> codec;
		protected final TriFunction<T, T, T, T> clamp;
		protected String comment;
		protected boolean sync;
		protected StreamCodec<ByteBuf, T> packetCodec;
		protected T min;
		protected T max;
		protected double step;
		protected BiFunction<T, T, Codec<T>> codecRange;

		public Builder(T defaultValue, Codec<T> codec, TriFunction<T, T, T, T> clamp) {
			this.defaultValue = defaultValue;
			this.codec = codec;
			this.clamp = clamp;
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
			this.min = min;
			this.max = max;
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

		public NumericValue.Builder<T> packetCodec(StreamCodec<ByteBuf, T> packetCodec) {
			this.packetCodec = packetCodec;
			return this;
		}

		public Value.Builder<List<T>> toList() {
			return new Value.Builder<>(List.of(defaultValue), Codec.list(codec));
		}

		private boolean noRange() {
			return min == null || max == null;
		}

		public NumericValue<T> build() {
			return new NumericValueImpl<>(
					defaultValue,
					codec,
					packetCodec,
					comment,
					sync,
					min, max,
					noRange() ? 0 : step / (max.doubleValue() - min.doubleValue()),
					clamp
			);
		}
	}
}
