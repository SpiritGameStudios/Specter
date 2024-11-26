package dev.spiritstudios.specter.impl.serialization.codec;

import com.mojang.serialization.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class KeyDispatchingCodec<T> extends MapCodec<T> {
	private final MapCodec<T> defaultCodec;
	private final Set<MapCodec<T>> possibleCodecs;
	private final Function<MapLike<?>, MapCodec<T>> dispatcher;

	public KeyDispatchingCodec(MapCodec<T> defaultCodec, List<MapCodec<T>> possibleCodecs, Function<MapLike<?>, MapCodec<T>> dispatcher) {
		this.defaultCodec = defaultCodec;
		this.possibleCodecs = new HashSet<>(possibleCodecs);
		this.dispatcher = dispatcher;
	}

	@Override
	public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
		return possibleCodecs.stream()
			.flatMap(codec -> codec.keys(ops))
			.distinct();
	}

	@Override
	public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
		MapCodec<T> codec = dispatcher.apply(input);
		if (!possibleCodecs.contains(codec)) return DataResult.error(() -> "Dispatcher returned unknown codec");
		return codec.decode(ops, input);
	}

	@Override
	public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
		return defaultCodec.encode(input, ops, prefix);
	}

	@Override
	public String toString() {
		return "KeyDispatchingCodec[%s]".formatted(possibleCodecs);
	}
}
