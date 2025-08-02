package dev.spiritstudios.specter.impl.debug;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import dev.spiritstudios.specter.impl.debug.command.ComponentsCommand;
import dev.spiritstudios.specter.impl.debug.command.HealCommand;
import dev.spiritstudios.specter.impl.debug.command.MetatagCommand;
import dev.spiritstudios.specter.impl.debug.item.LootLoaderItem;

public class SpecterDebug implements ModInitializer {
	public static final Item LOOT_LOADER = Registry.register(
			Registries.ITEM,
			Identifier.of("specter-debug", "loot_loader"),
			new LootLoaderItem(new Item.Settings()
					.maxCount(1)
					.rarity(Rarity.EPIC)
					.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("specter-debug", "loot_loader"))))
	);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			MetatagCommand.register(dispatcher);
			HealCommand.register(dispatcher);
			ComponentsCommand.register(dispatcher);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(entries -> {
			entries.add(LOOT_LOADER);
		});
	}
}
