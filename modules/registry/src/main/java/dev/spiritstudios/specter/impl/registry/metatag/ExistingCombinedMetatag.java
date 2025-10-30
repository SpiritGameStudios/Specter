package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public record ExistingCombinedMetatag<R, V>(
		ResourceKey<Registry<R>> registryKey,
		ResourceLocation id,
		Codec<V> codec,
		StreamCodec<RegistryFriendlyByteBuf, V> packetCodec,
		Supplier<@Unmodifiable Map<R, V>> existingGetter
) implements Metatag<R, V> {
	@Override
	public Optional<V> get(R entry) {
		return Optional.ofNullable(MetatagValueHolder.getOrCreate(registryKey).specter$getMetatagValue(this, entry))
				.or(() -> Optional.ofNullable(existingGetter.get().get(entry)));
	}

	@Override
	public boolean containsKey(R entry) {
		return MetatagValueHolder.getOrCreate(registryKey).specter$contains(this, entry) || existingGetter.get().containsKey(entry);
	}

	public Map<R, V> rawValues() {
		return Collections.unmodifiableMap(MetatagValueHolder.getOrCreate(registryKey).specter$getMetatagValues(this));
	}

	public Iterator<Map.Entry<R, V>> rawIterator() {
		return rawValues().entrySet().iterator();
	}

	@NotNull
	@Override
	public Iterator<Map.Entry<R, V>> iterator() {
		return Iterators.concat(
				rawIterator(),
				existingGetter.get().entrySet().iterator()
		);
	}

	@Override
	public @Unmodifiable Map<R, V> values() {
		Map<R, V> map = new Object2ObjectOpenHashMap<>(existingGetter.get());
		map.putAll(rawValues());

		return map;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ExistingCombinedMetatag<?, ?> that)) return false;
		return Objects.equals(id, that.id) && Objects.equals(registryKey, that.registryKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(registryKey, id);
	}
}
