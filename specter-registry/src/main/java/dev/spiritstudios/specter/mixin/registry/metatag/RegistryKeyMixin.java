package dev.spiritstudios.specter.mixin.registry.metatag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(RegistryKey.class)
public class RegistryKeyMixin<R> implements MetatagHolder<R> {
	@Unique
	private Map<Identifier, Metatag<R, ?>> metatags;

	@Unique
	private final Table<Metatag<R, ?>, R, Object> values = Tables.newCustomTable(new Object2ReferenceOpenHashMap<>(), Reference2ObjectOpenHashMap::new);

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(Identifier registry, Identifier value, CallbackInfo ci) {
		metatags = new Object2ObjectOpenHashMap<>();
	}

	@Override
	public void specter$registerMetatag(Metatag<R, ?> metatag) {
		metatags.put(metatag.getId(), metatag);
	}

	@Override
	public @Nullable Metatag<R, ?> specter$getMetatag(Identifier id) {
		return this.metatags.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V specter$getMetatagValue(Metatag<R, V> metatag, R entry) {
		return (V) values.get(metatag, entry);
	}

	@Override
	public Set<Map.Entry<Identifier, Metatag<R, ?>>> specter$getMetatags() {
		return ImmutableSet.copyOf(metatags.entrySet());
	}

	@Override
	public void specter$clearMetatag(Metatag<R, ?> metatag) {
		values.row(metatag).clear();
	}

	@Override
	public <T> void specter$putMetatagValue(Metatag<R, T> metatag, R entry, T value) {
		values.put(metatag, entry, value);
	}
}
