package dev.spiritstudios.testmod.item;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class SpecterItemTestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(
			BuiltInRegistries.ITEM,
			ResourceLocation.fromNamespaceAndPath("specter-item-testmod", "test_item"),
			new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("specter-item-testmod", "test_item"))))
		);
	}
}
