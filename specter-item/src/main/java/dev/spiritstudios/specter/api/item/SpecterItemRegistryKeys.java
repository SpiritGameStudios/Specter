package dev.spiritstudios.specter.api.item;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import dev.spiritstudios.specter.impl.core.Specter;

public final class SpecterItemRegistryKeys {
	public static final RegistryKey<Registry<DataItemGroup>> ITEM_GROUP = RegistryKey.ofRegistry(
			Specter.id("item_group"));
}
