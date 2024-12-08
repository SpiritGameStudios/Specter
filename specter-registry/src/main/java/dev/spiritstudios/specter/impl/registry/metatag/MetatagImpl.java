package dev.spiritstudios.specter.impl.registry.metatag;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.core.util.SpecterAssertions;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public record MetatagImpl<R, V>(
	Registry<R> registry,
	Identifier id,
	Codec<V> codec,
	PacketCodec<RegistryByteBuf, V> packetCodec,
	ResourceType side
) implements Metatag<R, V> {
	@Override
	public Optional<V> get(R entry) {
		if (this.side == ResourceType.CLIENT_RESOURCES) SpecterAssertions.assertClient();

		return Optional.ofNullable(MetatagValueHolder.getOrCreate(registry).specter$getMetatagValue(this, entry));
	}

	@NotNull
	@Override
	public Iterator<Entry<R, V>> iterator() {
		if (this.side == ResourceType.CLIENT_RESOURCES) SpecterAssertions.assertClient();

		return this.registry.stream().map(entry -> {
			V value = MetatagValueHolder.getOrCreate(registry).specter$getMetatagValue(this, entry);
			return value == null ? null : new Entry<>(entry, value);
		}).filter(Objects::nonNull).iterator();
	}

	@Override
	public void put(R entry, V value) {
		if (this.side == ResourceType.CLIENT_RESOURCES) SpecterAssertions.assertClient();

		if (this.registry.getId(entry) == null) throw new IllegalArgumentException("Entry is not in the registry");
		MetatagValueHolder.getOrCreate(registry).specter$putMetatagValue(this, entry, value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MetatagImpl<?, ?> that)) return false;
		return Objects.equals(id, that.id) && Objects.equals(registry.getKey(), that.registry.getKey());
	}

	@Override
	public int hashCode() {
		return Objects.hash(registry.getKey(), id);
	}
}
