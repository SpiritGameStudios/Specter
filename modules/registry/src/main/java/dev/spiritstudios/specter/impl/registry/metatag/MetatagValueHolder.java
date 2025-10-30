package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public final class MetatagValueHolder<R> {
	private final Table<Metatag<R, ?>, R, Object> values = Tables.newCustomTable(new Object2ReferenceOpenHashMap<>(), Reference2ObjectOpenHashMap::new);

	public static <R> MetatagValueHolder<R> getOrCreate(ResourceKey<Registry<R>> registry) {
		MetatagHolder<R> holder = MetatagHolder.of(registry);
		MetatagValueHolder<R> valueHolder = holder.specter$getValueHolder();
		if (valueHolder == null) {
			valueHolder = new MetatagValueHolder<>();
			holder.specter$setValueHolder(valueHolder);
		}

		return valueHolder;
	}

	@SuppressWarnings("unchecked")
	public <V> V specter$getMetatagValue(Metatag<R, V> metatag, R entry) {
		return (V) values.get(metatag, entry);
	}

	public boolean specter$contains(Metatag<R, ?> metatag, R entry) {
		return values.contains(metatag, entry);
	}

	public void specter$clearMetatag(Metatag<R, ?> metatag) {
		values.row(metatag).clear();
	}

	public <T> void specter$putMetatagValue(Metatag<R, T> metatag, R entry, T value) {
		values.put(metatag, entry, value);
	}

	@SuppressWarnings("unchecked")
	public <V> Map<R, V> specter$getMetatagValues(Metatag<R, V> metatag) {
		return (Map<R, V>) values.row(metatag);
	}
}
