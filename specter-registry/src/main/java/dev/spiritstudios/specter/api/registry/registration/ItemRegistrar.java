package dev.spiritstudios.specter.api.registry.registration;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface ItemRegistrar extends MinecraftRegistrar<Item> {
	@Override
	default Registry<Item> getRegistry() {
		return Registries.ITEM;
	}
}
