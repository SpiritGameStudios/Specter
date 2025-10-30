package dev.spiritstudios.specter.impl.item;

import net.minecraft.resources.ResourceKey;

public interface UnfrozenRegistry<T> {
	void specter$remove(ResourceKey<T> key);
}
