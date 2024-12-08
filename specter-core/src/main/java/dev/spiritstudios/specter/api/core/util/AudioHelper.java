package dev.spiritstudios.specter.api.core.util;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;
import net.minecraft.util.math.random.Random;

public final class AudioHelper {
	private AudioHelper() {
		throw new UnreachableException();
	}

	public static float randomPitch(Random random, float base, float maxDelta) {
		return base + (random.nextFloat() * 2 - 1) * maxDelta;
	}
}
