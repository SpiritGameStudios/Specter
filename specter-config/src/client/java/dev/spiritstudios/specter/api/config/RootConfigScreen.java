package dev.spiritstudios.specter.api.config;

import net.minecraft.client.gui.screen.Screen;

public class RootConfigScreen extends ConfigScreen {
	private final ConfigHolder<?, ?> holder;

	public RootConfigScreen(ConfigHolder<?, ?> holder, Screen parent) {
		super(holder.get(), holder.id().toTranslationKey(), parent);
		this.holder = holder;
	}

	@Override
	public void save() {
		this.holder.save();
	}
}
