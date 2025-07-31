package dev.spiritstudios.specter.impl.item;

import net.minecraft.registry.RegistryKey;

public interface UnfrozenRegistry<T> {
	void specter$remove(RegistryKey<T> key);
}
