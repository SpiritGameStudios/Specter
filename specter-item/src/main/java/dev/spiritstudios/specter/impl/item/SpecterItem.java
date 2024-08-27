package dev.spiritstudios.specter.impl.item;

import dev.spiritstudios.specter.api.item.ItemAttachments;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.ItemConvertible;

public class SpecterItem implements ModInitializer {
	public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap<>();

	@Override
	public void onInitialize() {
		ItemAttachments.init();

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> reloadMaps());
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> reloadMaps());
	}

	private void reloadMaps() {
		ITEM_TO_LEVEL_INCREASE_CHANCE.clear();

		ItemAttachments.COMPOSTING_CHANCE.forEach((entry) -> ITEM_TO_LEVEL_INCREASE_CHANCE.put(entry.key(), entry.value().floatValue()));
	}
}
