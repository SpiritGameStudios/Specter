package dev.spiritstudios.specter.impl.entity;

import dev.spiritstudios.specter.api.entity.EntityAttachments;
import net.fabricmc.api.ModInitializer;

public class SpecterEntity implements ModInitializer {
	@Override
	public void onInitialize() {
		EntityAttachments.init();
	}
}
