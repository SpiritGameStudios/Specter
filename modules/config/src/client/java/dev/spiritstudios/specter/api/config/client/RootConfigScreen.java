package dev.spiritstudios.specter.api.config.client;

import net.minecraft.client.gui.screens.Screen;

import dev.spiritstudios.specter.api.config.ConfigHolder;

public class RootConfigScreen extends ConfigScreen {
	private final ConfigHolder<?, ?> holder;

	public RootConfigScreen(ConfigHolder<?, ?> holder, Screen parent) {
		super(holder.get(), holder.id().toLanguageKey(), parent);
		this.holder = holder;
	}

	@Override
	public void save() {
		this.holder.save();
	}
}
