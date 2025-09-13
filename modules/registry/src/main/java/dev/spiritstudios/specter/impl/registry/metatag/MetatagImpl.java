package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Unmodifiable;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public record MetatagImpl<R, V>(
		RegistryKey<Registry<R>> registryKey,
		Identifier id,
		Codec<V> codec,
		PacketCodec<RegistryByteBuf, V> packetCodec
) implements Metatag<R, V> {
	@Override
	public Optional<V> get(R entry) {
		return Optional.ofNullable(MetatagValueHolder.getOrCreate(registryKey).specter$getMetatagValue(this, entry));
	}

	@Override
	public boolean containsKey(R entry) {
		return MetatagValueHolder.getOrCreate(registryKey).specter$contains(this, entry);
	}

	@Unmodifiable
	@Override
	public Map<R, V> values() {
		return Collections.unmodifiableMap(MetatagValueHolder.getOrCreate(registryKey).specter$getMetatagValues(this));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MetatagImpl<?, ?> that)) return false;
		return Objects.equals(id, that.id) && Objects.equals(registryKey, that.registryKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(registryKey, id);
	}
}
