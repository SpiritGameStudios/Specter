package dev.spiritstudios.specter.impl.biome;

import dev.spiritstudios.specter.api.biome.BiomeEffectsModificationManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;

public class SpecterBiomeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> BiomeEffectsModificationManager.apply(registries));
	}
}
