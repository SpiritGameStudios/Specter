package dev.spiritstudios.specter.api.registry.attachment;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentImpl;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;

/**
 * Represents a data attachment that can be attached to a registry object.
 * Attachments are used to store additional data about a registry object.
 * <p>
 * They are very similar to tags in that they can be used to store additional data about an object,
 * but Attachments can store data, unlike tags, which only store whether an object has a tag or not.
 *
 * @param <R> The type of registry object that this attachment can be attached to.
 * @param <V> The type of data stored in an attachment entry.
 */
public interface Attachment<R, V> extends Iterable<Attachment.Entry<R, V>> {
	static <R, V> Builder<R, V> builder(Registry<R> registry, Identifier id, Codec<V> codec, PacketCodec<RegistryByteBuf, V> packetCodec) {
		return new Builder<>(registry, id, codec, packetCodec);
	}

	Registry<R> getRegistry();

	Identifier getId();

	Codec<V> getCodec();

	PacketCodec<RegistryByteBuf, V> getPacketCodec();

	Optional<V> get(R entry);

	@NotNull
	Iterator<Entry<R, V>> iterator();

	void put(R entry, V value);

	record Entry<R, V>(R key, V value) {
	}

	final class Builder<R, V> {
		private final Registry<R> registry;
		private final Identifier id;
		private final Codec<V> codec;
		private final PacketCodec<RegistryByteBuf, V> packetCodec;

		private Builder(Registry<R> registry, Identifier id, Codec<V> codec, PacketCodec<RegistryByteBuf, V> packetCodec) {
			this.registry = registry;
			this.id = id;
			this.codec = codec;
			this.packetCodec = packetCodec;
		}

		public Attachment<R, V> build() {
			Attachment<R, V> attachment = new AttachmentImpl<>(registry, id, codec, packetCodec);
			AttachmentHolder.of(registry).specter$registerAttachment(attachment);
			return attachment;
		}
	}
}
