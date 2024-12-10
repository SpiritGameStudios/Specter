package dev.spiritstudios.specter.impl.item;

import dev.spiritstudios.specter.api.item.ItemMetatags;
import dev.spiritstudios.specter.api.item.SpecterItemRegistryKeys;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.ItemConvertible;

public class SpecterItem implements ModInitializer {
	public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap<>();

	@Override
	public void onInitialize() {
		ItemMetatags.init();

		SpecterReloadableRegistries.registerSynced(
			SpecterItemRegistryKeys.ITEM_GROUP,
			DataItemGroup.CODEC,
			DataItemGroup.PACKET_CODEC
		);

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> reload());
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> reload());
	}

	private void reload() {
		ITEM_TO_LEVEL_INCREASE_CHANCE.clear();
		ItemMetatags.COMPOSTING_CHANCE.forEach((entry) -> ITEM_TO_LEVEL_INCREASE_CHANCE.put(entry.key(), entry.value().floatValue()));
	}
}
