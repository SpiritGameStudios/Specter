package dev.spiritstudios.testmod.config;

import net.fabricmc.api.ClientModInitializer;

import dev.spiritstudios.specter.api.config.client.ModMenuHelper;

public class SpecterConfigTestmodClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModMenuHelper.addConfig("specter-config-testmod", TestConfig.TOML_HOLDER.id());
	}
}
