package dev.spiritstudios.testmod;

import net.fabricmc.api.ModInitializer;

public class SpecterConfigTestmod implements ModInitializer {
	@Override
	public void onInitialize() {
		TestConfig.TOML_HOLDER.save();
		TestConfig.JSON_HOLDER.save();
	}
}
