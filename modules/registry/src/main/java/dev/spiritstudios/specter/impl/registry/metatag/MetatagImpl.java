package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Unmodifiable;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public record MetatagImpl<R, V>(
		ResourceKey<Registry<R>> registryKey,
		ResourceLocation id,
		Codec<V> codec,
		StreamCodec<RegistryFriendlyByteBuf, V> packetCodec
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
