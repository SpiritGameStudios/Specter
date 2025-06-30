package dev.spiritstudios.specter.api.core.math;

import org.jetbrains.annotations.NotNull;

public interface Range<T extends Number & Comparable<T>> {
	default boolean contains(@NotNull T value) {
		return value.compareTo(min()) >= 0 && value.compareTo(max()) <= 0;
	}

	@NotNull T clamp(@NotNull T value);

	@NotNull T range();

	@NotNull T lerp(@NotNull T delta);

	@NotNull T map(@NotNull T value, @NotNull Range<T> from);

	@NotNull T min();

	@NotNull T max();
}
