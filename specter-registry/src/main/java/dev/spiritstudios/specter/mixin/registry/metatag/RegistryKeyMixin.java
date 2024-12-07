package dev.spiritstudios.specter.mixin.registry.metatag;

import com.google.common.collect.ImmutableSet;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Set;

@Mixin(RegistryKey.class)
public class RegistryKeyMixin<R> implements MetatagHolder<R> {
	@Unique
	private final Map<Identifier, Metatag<R, ?>> metatags = new Object2ObjectOpenHashMap<>();

	@Unique
	private MetatagValueHolder<R> valueHolder;

	@Override
	public void specter$registerMetatag(Metatag<R, ?> metatag) {
		metatags.put(metatag.id(), metatag);
	}

	@Override
	public @Nullable Metatag<R, ?> specter$getMetatag(Identifier id) {
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
	public Set<Map.Entry<Identifier, Metatag<R, ?>>> specter$getMetatags() {
		return ImmutableSet.copyOf(metatags.entrySet());
	}
}
