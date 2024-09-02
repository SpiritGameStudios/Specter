package dev.spiritstudios.specter.impl.registry.attachment;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
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
		clientGuard();

		return Optional.ofNullable(AttachmentHolder.of(registry).specter$getAttachmentValue(this, entry));
	}

	@Override
	public @NotNull Iterator<Entry<R, V>> iterator() {
		clientGuard();
		
		return this.registry.stream().map(entry -> {
			V value = (AttachmentHolder.of(registry).specter$getAttachmentValue(this, entry));
			return value == null ? null : new Entry<>(entry, value);
		}).filter(Objects::nonNull).iterator();
	}

	@Override
	public void put(R entry, V value) {
		clientGuard();

		if (this.registry.getId(entry) == null) throw new IllegalArgumentException("Entry is not in the registry");
		AttachmentHolder.of(registry).specter$putAttachmentValue(this, entry, value);
	}

	private void clientGuard() {
		EnvType current = FabricLoader.getInstance().getEnvironmentType();
		if (side == ResourceType.CLIENT_RESOURCES && current == EnvType.SERVER)
			throw new IllegalStateException("Attachment is not available on the current side");
	}
}
