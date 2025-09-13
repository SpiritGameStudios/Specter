package dev.spiritstudios.specter.impl.item;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

import dev.spiritstudios.specter.api.item.DataItemGroup;
import dev.spiritstudios.specter.api.item.ItemMetatags;
import dev.spiritstudios.specter.api.item.SpecterItemRegistryKeys;

public class SpecterItem implements ModInitializer {
	@Override
	public void onInitialize() {
		ItemMetatags.init();
		DynamicRegistries.registerSynced(SpecterItemRegistryKeys.ITEM_GROUP, DataItemGroup.CODEC);
	}
}
