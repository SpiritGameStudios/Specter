package dev.spiritstudios.specter.impl.item;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.item.ItemMetatags;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class SpecterItem implements ModInitializer {
	public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap<>();
	public static final RegistryKey<Registry<DataItemGroup>> ITEM_GROUP_KEY = RegistryKey.ofRegistry(
		Identifier.of(SpecterGlobals.MODID, "item_group"));

	@Override
	public void onInitialize() {
		ItemMetatags.init();

//		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ItemGroupReloader());
		SpecterReloadableRegistries.registerSynced(
			ITEM_GROUP_KEY,
			DataItemGroup.CODEC,
			DataItemGroup.PACKET_CODEC
		);

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> reload(server.getRegistryManager()));
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> reload(server.getRegistryManager()));
	}

	private void reload(DynamicRegistryManager registryManager) {
		ITEM_TO_LEVEL_INCREASE_CHANCE.clear();
		ItemMetatags.COMPOSTING_CHANCE.forEach((entry) -> ITEM_TO_LEVEL_INCREASE_CHANCE.put(entry.key(), entry.value().floatValue()));
	}
}
