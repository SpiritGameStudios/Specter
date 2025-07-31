package dev.spiritstudios.specter.impl.item;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;

import dev.spiritstudios.specter.api.item.DataItemGroup;
import dev.spiritstudios.specter.mixin.item.SimpleRegistryAccessor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

import dev.spiritstudios.specter.api.item.ItemMetatags;
import dev.spiritstudios.specter.api.item.SpecterItemRegistryKeys;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;

import java.util.Map;

public class SpecterItem implements ModInitializer {
	@Override
	public void onInitialize() {
		ItemMetatags.init();
		DynamicRegistries.registerSynced(SpecterItemRegistryKeys.ITEM_GROUP, DataItemGroup.CODEC);

		DynamicRegistrySetupCallback.EVENT.register(view -> {
			if (!(Registries.ITEM_GROUP instanceof SimpleRegistry<ItemGroup> registry)) throw new UnreachableException();
			SimpleRegistryAccessor accessor = (SimpleRegistryAccessor)registry;
			@SuppressWarnings("unchecked") UnfrozenRegistry<ItemGroup> unfrozen = ((UnfrozenRegistry<ItemGroup>) registry);

			view.registerEntryAdded(SpecterItemRegistryKeys.ITEM_GROUP, (rawId, id, object) -> {
				if (accessor.getFrozen()) { // if the registry is frozen, this is the first entry
					accessor.setFrozen(false);

					// remove data item groups, we are about to load them in again
					for (Map.Entry<RegistryKey<ItemGroup>, ItemGroup> entry : registry.getEntrySet()) {
						if (entry.getValue() instanceof DataItemGroup) {
							unfrozen.specter$remove(entry.getKey());
						}
					}
				}

				RegistryKey<ItemGroup> key = RegistryKey.of(RegistryKeys.ITEM_GROUP, id);
				Registry.register(Registries.ITEM_GROUP, key, object);
			});

			// for some reason this gets called after the registry entries are added?
			// I'm pretty sure this event is meant to fire just before load
			accessor.setFrozen(true);
		});
	}
}
