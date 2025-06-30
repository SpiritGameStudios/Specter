package dev.spiritstudios.specter.api.core.math;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.math.MathHelper;

public record FloatRange(@NotNull Float min, @NotNull Float max) implements Range<Float> {
	/**
	 * A range from 0 to 1
	 */
	public static final FloatRange ZERO_ONE = new FloatRange(0.0F, 1.0F);

	/**
	 * A range from {@link Float#MIN_VALUE} to {@link Float#MAX_VALUE}
	 */
	public static final FloatRange FULL = new FloatRange(Float.MIN_VALUE, Float.MAX_VALUE);


	@Override
	public @NotNull Float clamp(@NotNull Float value) {
		return Math.clamp(value, min, max);
	}

	@Override
	public @NotNull Float range() {
		return max - min;
	}

	@Override
	public @NotNull Float lerp(@NotNull Float delta) {
		return MathHelper.lerp(delta, min, max);
	}

	@Override
	public @NotNull Float map(@NotNull Float value, @NotNull Range<Float> from) {
		return lerp((value - from.min()) / (from.max() - from.min()));
	}
}
