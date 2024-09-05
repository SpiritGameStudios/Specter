package dev.spiritstudios.specter.impl.registry.metatag;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface MetatagHolder<R> {
	@SuppressWarnings("unchecked")
	static <R> MetatagHolder<R> of(Registry<R> registry) {
		return (MetatagHolder<R>) registry.getKey();
	}

	void specter$registerMetatag(Metatag<R, ?> metatag);

	@Nullable
	Metatag<R, ?> specter$getMetatag(Identifier id);

	<V> V specter$getMetatagValue(Metatag<R, V> metatag, R entry);

	Set<Map.Entry<Identifier, Metatag<R, ?>>> specter$getMetatags();

	void specter$clearMetatag(Metatag<R, ?> metatag);

	<T> void specter$putMetatagValue(Metatag<R, T> metatag, R entry, T value);
}
