package dev.spiritstudios.specter.impl.debug;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import dev.spiritstudios.specter.impl.debug.command.ComponentsCommand;
import dev.spiritstudios.specter.impl.debug.command.HealCommand;
import dev.spiritstudios.specter.impl.debug.command.MetatagCommand;
import dev.spiritstudios.specter.impl.debug.item.LootLoaderItem;

public class SpecterDebug implements ModInitializer {
	public static final Item LOOT_LOADER = Registry.register(
			BuiltInRegistries.ITEM,
			ResourceLocation.fromNamespaceAndPath("specter-debug", "loot_loader"),
			new LootLoaderItem(new Item.Properties()
					.stacksTo(1)
					.rarity(Rarity.EPIC)
					.setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("specter-debug", "loot_loader"))))
	);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			MetatagCommand.register(dispatcher);
			HealCommand.register(dispatcher);
			ComponentsCommand.register(dispatcher);
		});

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(entries -> {
			entries.accept(LOOT_LOADER);
		});
	}
}
