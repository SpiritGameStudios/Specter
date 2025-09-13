package dev.spiritstudios.specter.impl.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

import dev.spiritstudios.specter.api.core.SpecterGlobals;

public class Specter implements ModInitializer {
	public static final String MODID = "specter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Identifier id(String path) {
		return Identifier.of(MODID, path);
	}

	@Override
	public void onInitialize() {
		if (SpecterGlobals.FORGE)
			Specter.LOGGER.warn("Sinytra Connector detected. This is not officially supported by Specter and may not function correctly. Please do not report bugs to Spirit Studios while using Sinytra Connector.");

		if (SpecterGlobals.QUILT)
			Specter.LOGGER.warn("Quilt Loader detected. This should work fine, but has not been tested. Please report any issues to Spirit Studios.");
	}
}
