package dev.spiritstudios.specter.mixin.registry.metatag;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.registry.entry.RegistryEntry;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetataggedEntry;

@Mixin(RegistryEntry.Reference.class)
public abstract class RegistryEntry$ReferenceMixin<T> implements MetataggedEntry<T> {
	@Unique
	private @Nullable Map<Metatag<T, ?>, Object> metatagValues;

	@SuppressWarnings("unchecked")
	@Override
	public <V> V specter$getMetatagValue(Metatag<T, V> metatag) {
		if (metatagValues == null) return null;
		return (V) metatagValues.get(metatag);
	}

	@Override
	public void specter$setMetatagValues(Map<Metatag<T, ?>, Object> map) {
		this.metatagValues = map;
	}
}
