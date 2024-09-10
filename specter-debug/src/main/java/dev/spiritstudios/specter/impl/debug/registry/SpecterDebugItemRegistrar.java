package dev.spiritstudios.specter.impl.debug.registry;

import dev.spiritstudios.specter.api.registry.registration.ItemRegistrar;
import dev.spiritstudios.specter.impl.debug.item.LootLoaderItem;
import net.minecraft.item.Item;

public class SpecterDebugItemRegistrar implements ItemRegistrar {
	public static final LootLoaderItem LOOT_LOADER = new LootLoaderItem(new Item.Settings().maxCount(1));
}
