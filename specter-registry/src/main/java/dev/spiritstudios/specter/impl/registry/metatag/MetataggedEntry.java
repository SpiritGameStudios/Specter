package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.entry.RegistryEntry;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public interface MetataggedEntry<T> {
	<V> @Nullable V specter$getMetatagValue(Metatag<T, V> metatag);
	void specter$setMetatagValues(Map<Metatag<T, ?>, Object> map);

	@SuppressWarnings("unchecked")
	static <R> MetataggedEntry<R> of(RegistryEntry.Reference<R> entry) {
		return (MetataggedEntry<R>) entry;
	}

}
