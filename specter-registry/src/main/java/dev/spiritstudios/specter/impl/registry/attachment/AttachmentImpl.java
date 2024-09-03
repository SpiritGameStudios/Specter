package dev.spiritstudios.specter.impl.registry.attachment;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.core.util.SpecterAssertions;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public record AttachmentImpl<R, V>(Registry<R> registry, Identifier id, Codec<V> codec,
								   PacketCodec<RegistryByteBuf, V> packetCodec,
								   ResourceType side) implements Attachment<R, V> {
	@Override
	public Registry<R> getRegistry() {
		return registry;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public Codec<V> getCodec() {
		return codec;
	}

	@Override
	public PacketCodec<RegistryByteBuf, V> getPacketCodec() {
		return packetCodec;
	}

	@Override
	public ResourceType getSide() {
		return side;
	}

	@Override
	public Optional<V> get(R entry) {
		if (this.side == ResourceType.CLIENT_RESOURCES) SpecterAssertions.assertClient();

		return Optional.ofNullable(AttachmentHolder.of(registry).specter$getAttachmentValue(this, entry));
	}

	@Override
	public @NotNull Iterator<Entry<R, V>> iterator() {
		if (this.side == ResourceType.CLIENT_RESOURCES) SpecterAssertions.assertClient();

		return this.registry.stream().map(entry -> {
			V value = (AttachmentHolder.of(registry).specter$getAttachmentValue(this, entry));
			return value == null ? null : new Entry<>(entry, value);
		}).filter(Objects::nonNull).iterator();
	}

	@Override
	public void put(R entry, V value) {
		if (this.side == ResourceType.CLIENT_RESOURCES) SpecterAssertions.assertClient();

		if (this.registry.getId(entry) == null) throw new IllegalArgumentException("Entry is not in the registry");
		AttachmentHolder.of(registry).specter$putAttachmentValue(this, entry, value);
	}
}
