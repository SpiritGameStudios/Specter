package dev.spiritstudios.specter.impl.debug;

import dev.spiritstudios.specter.impl.debug.command.ComponentsCommand;
import dev.spiritstudios.specter.impl.debug.command.HealCommand;
import dev.spiritstudios.specter.impl.debug.command.MetatagCommand;
import dev.spiritstudios.specter.impl.debug.item.LootLoaderItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class SpecterDebug implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			MetatagCommand.register(dispatcher);
			HealCommand.register(dispatcher);
			ComponentsCommand.register(dispatcher);
		});

		Registry.register(
			Registries.ITEM,
			Identifier.of("specter_debug", "loot_loader"),
			new LootLoaderItem(new Item.Settings().maxCount(1)
				.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("specter_debug", "loot_loader"))))
		);
	}
}
