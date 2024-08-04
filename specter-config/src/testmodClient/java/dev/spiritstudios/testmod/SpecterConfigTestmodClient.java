package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.ModMenuHelper;
import net.fabricmc.api.ClientModInitializer;

public class SpecterConfigTestmodClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModMenuHelper.addConfig("specter-config-testmod", TestConfig.class);
	}
}