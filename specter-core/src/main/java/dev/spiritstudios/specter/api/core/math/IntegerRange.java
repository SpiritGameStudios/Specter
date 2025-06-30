package dev.spiritstudios.specter.api.core.math;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.math.MathHelper;

public record IntegerRange(@NotNull Integer min, @NotNull Integer max) implements Range<Integer> {
	/**
	 * A range from {@link Integer#MIN_VALUE} to {@link Integer#MAX_VALUE}
	 */
	public static final IntegerRange FULL = new IntegerRange(Integer.MIN_VALUE, Integer.MAX_VALUE);

	@Override
	public @NotNull Integer clamp(@NotNull Integer value) {
		return MathHelper.clamp(value, min, max);
	}

	@Override
	public @NotNull Integer range() {
		return max - min;
	}

	@Override
	public @NotNull Integer lerp(@NotNull Integer delta) {
		return MathHelper.lerp(delta, min, max);
	}

	@Override
	public @NotNull Integer map(@NotNull Integer value, @NotNull Range<Integer> from) {
		return lerp((value - from.min()) / (from.max() - from.min()));
	}
}
