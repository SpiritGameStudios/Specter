package dev.spiritstudios.specter.api.core.math;

import org.jetbrains.annotations.NotNull;

public record LongRange(@NotNull Long min, @NotNull Long max) implements Range<Long> {
	/**
	 * A range from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE}
	 */
	public static final LongRange FULL = new LongRange(Long.MIN_VALUE, Long.MAX_VALUE);

	@Override
	public @NotNull Long clamp(@NotNull Long value) {
		return Math.clamp(value, min, max);
	}

	@Override
	public @NotNull Long range() {
		return max - min;
	}

	@Override
	public @NotNull Long lerp(@NotNull Long delta) {
		return min + delta * (max - min);
	}

	@Override
	public @NotNull Long map(@NotNull Long value, @NotNull Range<Long> from) {
		return lerp((value - from.min()) / (from.max() - from.min()));
	}
}
