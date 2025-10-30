package dev.spiritstudios.specter.mixin.registry.metatag;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;

@Mixin(ResourceKey.class)
public abstract class RegistryKeyMixin<R> implements MetatagHolder<R> {
	@Unique
	private @Nullable Map<ResourceLocation, Metatag<R, ?>> metatags;

	@Unique
	private MetatagValueHolder<R> valueHolder;

	@Override
	public void specter$registerMetatag(Metatag<R, ?> metatag) {
		if (metatags == null) metatags = new Object2ObjectOpenHashMap<>();
		metatags.put(metatag.id(), metatag);
	}

	@Override
	public @Nullable Metatag<R, ?> specter$getMetatag(ResourceLocation id) {
		if (metatags == null) return null;
		return this.metatags.get(id);
	}

	@Override
	public void specter$setValueHolder(MetatagValueHolder<R> valueHolder) {
		this.valueHolder = valueHolder;
	}

	@Override
	public @Nullable MetatagValueHolder<R> specter$getValueHolder() {
		return valueHolder;
	}

	@Override
	public Set<Map.Entry<ResourceLocation, Metatag<R, ?>>> specter$getMetatags() {
		if (metatags == null) return Collections.emptySet();
		return ImmutableSet.copyOf(metatags.entrySet());
	}
}
