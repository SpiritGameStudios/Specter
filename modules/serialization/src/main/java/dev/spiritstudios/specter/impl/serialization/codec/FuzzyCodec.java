package dev.spiritstudios.specter.impl.serialization.codec;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.serialization.*;

public class FuzzyCodec<T> extends MapCodec<T> {
	private final List<MapCodec<? extends T>> codecs;
	private final Function<T, MapEncoder<? extends T>> codecGetter;

	public FuzzyCodec(List<MapCodec<? extends T>> codecs, Function<T, MapEncoder<? extends T>> codecGetter) {
		this.codecs = codecs;
		this.codecGetter = codecGetter;
	}

	@Override
	public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
		return this.codecs.stream()
			.flatMap(codec -> codec.keys(ops))
			.distinct();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
		for (MapDecoder<? extends T> decoder : this.codecs) {
			DataResult<? extends T> result = decoder.decode(ops, input);
			if (result.result().isPresent()) return (DataResult<T>) result;
		}

		return DataResult.error(() -> "No matching codec found");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
		return ((MapEncoder<T>) this.codecGetter.apply(input)).encode(input, ops, prefix);
	}

	@Override
	public String toString() {
		return "FuzzyCodec[%s]".formatted(this.codecs);
	}
}
