package dev.spiritstudios.specter.api.item;

import dev.spiritstudios.specter.impl.core.Specter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class SpecterItemRegistryKeys {
	public static final ResourceKey<Registry<DataItemGroup>> ITEM_GROUP = ResourceKey.createRegistryKey(
			Specter.id("item_group"));
}
