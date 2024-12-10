package dev.spiritstudios.specter.api.item;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.item.DataItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class SpecterItemRegistryKeys {
	public static final RegistryKey<Registry<DataItemGroup>> ITEM_GROUP = RegistryKey.ofRegistry(
		Identifier.of(SpecterGlobals.MODID, "item_group"));
}
