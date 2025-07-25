package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public interface MetatagHolder<R> {
	@SuppressWarnings("unchecked")
	static <R> MetatagHolder<R> of(RegistryKey<? extends Registry<R>> registry) {
		return (MetatagHolder<R>) registry;
	}

	static MetatagHolder<?> ofAny(RegistryKey<? extends Registry<?>> registry) {
		return (MetatagHolder<?>) registry;
	}

	void specter$registerMetatag(Metatag<R, ?> metatag);

	Set<Map.Entry<Identifier, Metatag<R, ?>>> specter$getMetatags();

	@Nullable
	Metatag<R, ?> specter$getMetatag(Identifier id);

	void specter$setValueHolder(MetatagValueHolder<R> valueHolder);

	@Nullable MetatagValueHolder<R> specter$getValueHolder();
}
