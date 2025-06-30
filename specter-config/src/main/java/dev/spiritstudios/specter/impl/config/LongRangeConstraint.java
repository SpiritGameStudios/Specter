package dev.spiritstudios.specter.impl.config;

import com.mojang.serialization.DataResult;

import dev.spiritstudios.specter.api.config.Constraint;

public record LongRangeConstraint(long min, long max) implements Constraint<Long> {
	public LongRangeConstraint {
		if (min > max) throw new IllegalArgumentException("min > max");

	}

	@Override
	public DataResult<Long> test(Long value) {
		if (value >= min && value <= max) return DataResult.success(value);
		return DataResult.error(() ->
				"Value %s out of bounds for range of %s to %s.".formatted(value, min, max));
	}
}
