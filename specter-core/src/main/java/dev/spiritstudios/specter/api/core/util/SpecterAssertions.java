package dev.spiritstudios.specter.api.core.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class SpecterAssertions {
	private SpecterAssertions() {
	}

	public static void assertThrows(Runnable runnable) {
		assertThrows(Throwable.class, runnable);
	}

	public static <T extends Throwable> T assertThrows(Class<T> expectedType, Runnable runnable) {
		return assertThrows(expectedType, runnable, "Expected exception of type " + expectedType.getName());
	}

	public static <T extends Throwable> T assertThrows(Class<T> expectedType, Runnable runnable, String message) {
		try {
			runnable.run();
		} catch (Throwable exception) {
			if (expectedType.isInstance(exception)) return expectedType.cast(exception);
			throw new AssertionError(message, exception);
		}

		throw new AssertionError(message);
	}

	public static void assertClient() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
			throw new AssertionError("This method can only be called on the client side.");
	}

	public static void assertServer() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER)
			throw new AssertionError("This method can only be called on the server side.");
	}
}
