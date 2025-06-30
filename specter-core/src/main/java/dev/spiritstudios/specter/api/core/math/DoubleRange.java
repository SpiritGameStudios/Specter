package dev.spiritstudios.specter.api.core.math;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.math.MathHelper;

public record DoubleRange(@NotNull Double min, @NotNull Double max) implements Range<Double> {
	/**
	 * A range from 0 to 1
	 */
	public static final DoubleRange ZERO_ONE = new DoubleRange(0.0, 1.0);


	/**
	 * A range from {@link Double#MIN_VALUE} to {@link Double#MAX_VALUE}
	 */
	public static final DoubleRange FULL = new DoubleRange(Double.MIN_VALUE, Double.MAX_VALUE);


	@Override
	public @NotNull Double clamp(@NotNull Double value) {
		return Math.clamp(value, min, max);
	}

	@Override
	public @NotNull Double range() {
		return max - min;
	}

	@Override
	public @NotNull Double lerp(@NotNull Double delta) {
		return MathHelper.lerp(delta, min, max);
	}

	@Override
	public @NotNull Double map(@NotNull Double value, @NotNull Range<Double> from) {
		return lerp((value - from.min()) / (from.max() - from.min()));
	}
}
