package dev.spiritstudios.specter.impl.config;

import com.mojang.serialization.DataResult;

import dev.spiritstudios.specter.api.config.Constraint;

public record IntegerRangeConstraint(int min, int max) implements Constraint<Integer> {
	public IntegerRangeConstraint {
		if (min > max) throw new IllegalArgumentException("min > max");
	}

	@Override
	public DataResult<Integer> test(Integer value) {
		if (value >= min && value <= max) return DataResult.success(value);
		return DataResult.error(() ->
				"Value %s out of bounds for range of %s to %s.".formatted(value, min, max));
	}
}
