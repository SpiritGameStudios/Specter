package dev.spiritstudios.specter.impl.entity;

import net.fabricmc.api.ModInitializer;

import dev.spiritstudios.specter.api.entity.EntityMetatags;

public class SpecterEntity implements ModInitializer {
	@Override
	public void onInitialize() {
		EntityMetatags.init();
	}
}
