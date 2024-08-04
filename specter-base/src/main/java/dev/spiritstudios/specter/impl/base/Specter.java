package dev.spiritstudios.specter.impl.base;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Specter implements ModInitializer {
	public static final String MODID = "specter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final boolean DEBUG;

	static {
		boolean debug;
		debug = FabricLoader.getInstance().isDevelopmentEnvironment();
		if (System.getProperty("specter.debug") != null) debug = Boolean.getBoolean("specter.debug");

		DEBUG = debug;
	}

	@Override
	public void onInitialize() {

	}
}
