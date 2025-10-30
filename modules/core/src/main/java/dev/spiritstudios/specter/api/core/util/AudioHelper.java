package dev.spiritstudios.specter.api.core.util;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;
import net.minecraft.util.RandomSource;

public final class AudioHelper {
	private AudioHelper() {
		throw new UnreachableException();
	}

	public static float randomPitch(RandomSource random, float base, float maxDelta) {
		return base + (random.nextFloat() * 2 - 1) * maxDelta;
	}
}
