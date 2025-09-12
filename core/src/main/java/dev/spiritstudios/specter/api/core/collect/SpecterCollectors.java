package dev.spiritstudios.specter.api.core.collect;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @see java.util.stream.Collectors
 * @see java.util.stream.Collector
 */
public final class SpecterCollectors {
	public static <T, K, V> Collector<T, ?, Map<K, V>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper, Supplier<Map<K, V>> mapFactory) {
		return Collectors.toMap(
				keyMapper, valueMapper,
				(first, second) -> first,
				mapFactory
		);
	}
}
