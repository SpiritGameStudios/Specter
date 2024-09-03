package dev.spiritstudios.specter.api.registry.attachment;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
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

	ResourceType getSide();

	Optional<V> get(R entry);

	@NotNull
	Iterator<Entry<R, V>> iterator();

	/**
	 * Puts a value into this attachment for the given registry object.
	 * <p>
	 * Do not use this unless the data you are adding is generated at runtime.
	 * Attachments should always be defined in data packs when possible.
	 * </p>
	 *
	 * @param entry The registry object to put the value for.
	 * @param value The value to put.
	 */
	void put(R entry, V value);

	record Entry<R, V>(R key, V value) {
	}

	final class Builder<R, V> {
		private final Registry<R> registry;
		private final Identifier id;
		private final Codec<V> codec;
		private final PacketCodec<RegistryByteBuf, V> packetCodec;
		private ResourceType side = ResourceType.SERVER_DATA;

		private Builder(Registry<R> registry, Identifier id, Codec<V> codec, PacketCodec<RegistryByteBuf, V> packetCodec) {
			this.registry = registry;
			this.id = id;
			this.codec = codec;
			this.packetCodec = packetCodec;
		}

		/**
		 * Sets the side that this attachment is intended for. Defaults to {@link EnvType#SERVER}.
		 * <p>
		 * Server-side attachments are stored in data packs and are sent to clients when they connect. <br>
		 * Client-side attachments are stored in resource packs and as such are only available on the client.
		 * </p>
		 *
		 * @param side The side that this attachment is intended for.
		 * @return This builder.
		 */
		public Builder<R, V> side(ResourceType side) {
			this.side = side;
			return this;
		}

		public Attachment<R, V> build() {
			Attachment<R, V> attachment = new AttachmentImpl<>(registry, id, codec, packetCodec, side);

			if (side == ResourceType.CLIENT_RESOURCES && FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
				SpecterGlobals.LOGGER.warn(
					"Client-side attachment {} is being registered on the server. This should only be done on the client.",
					id
				);

				SpecterGlobals.LOGGER.warn("This warning may be changed to an exception in the future.");
			}

			AttachmentHolder.of(registry).specter$registerAttachment(attachment);
			return attachment;
		}
	}
}
