package dev.spiritstudios.testmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class SpecterItemTestmod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(
			Registries.ITEM,
			Identifier.of("specter-item-testmod", "test_item"),
			new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("specter-item-testmod", "test_item"))))
		);
	}
}
