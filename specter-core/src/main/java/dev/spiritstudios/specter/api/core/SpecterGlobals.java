package dev.spiritstudios.specter.api.core;

import net.fabricmc.loader.api.FabricLoader;

public final class SpecterGlobals {
	public static final boolean DEBUG = System.getProperty("specter.debug") != null ?
			Boolean.getBoolean("specter.debug") :
			FabricLoader.getInstance().isDevelopmentEnvironment();

	/**
	 * Whether the game is running on Forge with Sinytra Connector
	 * If you don't want to provide support for Connector, it's recommended to either log a warning or mark "connector" as incompatible in your fabric.mod.json
	 */
	public static final boolean FORGE = FabricLoader.getInstance().isModLoaded("connector");
	/**
	 * Whether the game is running on Quilt
	 * If you don't want to provide support for Quilt, it's recommended to either log a warning or mark "quilt_loader" as incompatible in your fabric.mod.json
	 */
	public static final boolean QUILT = FabricLoader.getInstance().isModLoaded("quilt_loader") && !FORGE; // !FORGE just in case connector ever supports Quilt
	public static final boolean FABRIC = FabricLoader.getInstance().isModLoaded("fabricloader") && !FORGE && !QUILT;
}
