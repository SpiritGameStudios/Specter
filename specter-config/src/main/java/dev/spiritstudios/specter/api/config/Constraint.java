package dev.spiritstudios.specter.api.config;

import com.mojang.serialization.DataResult;

import dev.spiritstudios.specter.impl.config.DoubleRangeConstraint;
import dev.spiritstudios.specter.impl.config.FloatRangeConstraint;
import dev.spiritstudios.specter.impl.config.IntegerRangeConstraint;
import dev.spiritstudios.specter.impl.config.LongRangeConstraint;

@FunctionalInterface
public interface Constraint<T> {
	static Constraint<Integer> range(int min, int max) {
		return new IntegerRangeConstraint(min, max);
	}

	static Constraint<Float> range(float min, float max) {
		return new FloatRangeConstraint(min, max);
	}

	static Constraint<Double> range(double min, double max) {
		return new DoubleRangeConstraint(min, max);
	}

	static Constraint<Long> range(long min, long max) {
		return new LongRangeConstraint(min, max);
	}

	DataResult<T> test(T value);
}
