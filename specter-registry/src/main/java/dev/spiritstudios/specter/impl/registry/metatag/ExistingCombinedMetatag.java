package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public record ExistingCombinedMetatag<R, V>(
		RegistryKey<Registry<R>> registryKey,
		Identifier id,
		Codec<V> codec,
		PacketCodec<RegistryByteBuf, V> packetCodec,
		Supplier<@Unmodifiable Map<R, V>> existingGetter
) implements Metatag<R, V> {
	@Override
	public Optional<V> get(R entry) {
		return Optional.ofNullable(MetatagValueHolder.getOrCreate(registryKey).specter$getMetatagValue(this, entry))
				.or(() -> Optional.ofNullable(existingGetter.get().get(entry)));
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
