package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.ModMenuHelper;
import dev.spiritstudios.specter.api.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;

public class SpecterConfigTestmodClient implements ClientModInitializer {
	public static final GetTestConfig CONFIG = ConfigManager.getConfig(GetTestConfig.class);

	@Override
	public void onInitializeClient() {
		ModMenuHelper.addConfig("specter-config-testmod", CONFIG.getId());
	}
}
