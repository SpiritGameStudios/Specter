package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.Map;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public interface MetatagHolder<R> {
	@SuppressWarnings("unchecked")
	static <R> MetatagHolder<R> of(ResourceKey<? extends Registry<R>> registry) {
		return (MetatagHolder<R>) registry;
	}

	static MetatagHolder<?> ofAny(ResourceKey<? extends Registry<?>> registry) {
		return (MetatagHolder<?>) registry;
	}

	void specter$registerMetatag(Metatag<R, ?> metatag);

	Set<Map.Entry<ResourceLocation, Metatag<R, ?>>> specter$getMetatags();

	@Nullable
	Metatag<R, ?> specter$getMetatag(ResourceLocation id);

	void specter$setValueHolder(MetatagValueHolder<R> valueHolder);

	@Nullable MetatagValueHolder<R> specter$getValueHolder();
}
