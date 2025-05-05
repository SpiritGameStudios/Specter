package dev.spiritstudios.specter.impl.core;

import net.fabricmc.api.ModInitializer;

import dev.spiritstudios.specter.api.core.SpecterGlobals;

public class Specter implements ModInitializer {
	@Override
	public void onInitialize() {
		if (SpecterGlobals.FORGE)
			SpecterGlobals.LOGGER.warn("Sinytra Connector detected. This is not officially supported by Specter and may not function correctly. Please do not report bugs to Spirit Studios while using Sinytra Connector.");

		if (SpecterGlobals.QUILT)
			SpecterGlobals.LOGGER.warn("Quilt Loader detected. This should work fine, but has not been tested. Please report any issues to Spirit Studios.");
	}
}
