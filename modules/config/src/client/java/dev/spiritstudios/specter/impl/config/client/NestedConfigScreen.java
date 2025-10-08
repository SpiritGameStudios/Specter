package dev.spiritstudios.specter.impl.config.client;

import net.minecraft.client.gui.screen.Screen;

import dev.spiritstudios.specter.api.config.NestedConfig;
import dev.spiritstudios.specter.api.config.client.ConfigScreen;

public class NestedConfigScreen extends ConfigScreen {
	public NestedConfigScreen(NestedConfig<?> config, String id, Screen parent) {
		super(config, id, parent);
		if (!(this.parent instanceof ConfigScreen))
			throw new IllegalArgumentException("Parent of NestedConfigScreen must be a ConfigScreen");
	}

	@Override
	public void save() {
		((ConfigScreen) this.parent).save();
	}
}
