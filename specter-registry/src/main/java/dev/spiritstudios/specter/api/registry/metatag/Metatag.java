package dev.spiritstudios.specter.api.registry.metatag;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.registry.metatag.ExistingCombinedMetatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagImpl;

/**
 * Metatags are used to store additional data about a registry object.
 * <p>
 * They are very similar to tags in that they can be used to store additional data about an object,
 * but Metatags can store data, unlike tags, which only store whether an object has a tag or not.
 * <p>
 * A regular tag can be thought of like an array of registry entries, a metatag on the other hand could be compared to a map with registry entries as keys.
 * <h2>Basic usage</h2>
 * To create your own metatag, use {@link Metatag#builder(Registry, Identifier, Codec)}. Your metatag will be automatically registered upon calling {@link Builder#build()}.
 * <h2>Client-sided usage</h2>
 * Metatags are not accessible to the client by default. To use them on the client, you must either:
 * <li>Make your metatag client side only with {@link Builder#side(ResourceType)}, moving it from datapack to resource pack.</li>
 * <li>Provide a {@link PacketCodec} for your metatag with {@link Builder#packetCodec(PacketCodec)}, allowing it to be synced with the client when it is loaded/reloaded.</li>
 *
 * @param <R> The type of registry that this metatag can be attached to.
 * @param <V> The type of data stored in a metatag entry.
 * @see MetatagEvents
 */
public interface Metatag<R, V> extends Iterable<Metatag.Entry<R, V>> {
	static <R, V> Builder<R, V> builder(Registry<R> registry, Identifier id, Codec<V> codec) {
		return new Builder<>(registry, id, codec);
	}

	Registry<R> registry();

	Identifier id();

	Codec<V> codec();

	PacketCodec<RegistryByteBuf, V> packetCodec();

	ResourceType side();

	Optional<V> get(R entry);

	@Override
	@NotNull
	Iterator<Entry<R, V>> iterator();

	/**
	 * Puts a value into this metatag for the given registry.
	 * <p>
	 * Do not use this unless the data you are adding is generated at runtime.
	 * Metatags should always be defined in data packs when possible.
	 * </p>
	 *
	 * @param entry The registry to attach the entry to.
	 * @param value The value to put.
	 */
	void put(R entry, V value);

	record Entry<R, V>(R key, V value) {
	}

	final class Builder<R, V> {
		private final Registry<R> registry;
		private final Identifier id;
		private final Codec<V> codec;
		private @Nullable PacketCodec<RegistryByteBuf, V> packetCodec;
		private ResourceType side = ResourceType.SERVER_DATA;
		private Function<R, V> existingGetter;
		private Supplier<Iterator<Entry<R, V>>> existingIterator;

		private Builder(Registry<R> registry, Identifier id, Codec<V> codec) {
			this.registry = registry;
			this.id = id;
			this.codec = codec;
		}

		/**
		 * Sets the side that this metatag is intended for. Defaults to {@link EnvType#SERVER}.
		 * <p>
		 * Server-side metatags are stored in data packs and (provided a {@link PacketCodec} is provided) are sent to clients when they connect. <br>
		 * Client-side metatags are stored in resource packs and as such are only available on the client.
		 * </p>
		 *
		 * @param side The side that this metatag is intended for.
		 * @return This builder.
		 */
		public Builder<R, V> side(ResourceType side) {
			this.side = side;
			return this;
		}

		public Builder<R, V> packetCodec(PacketCodec<RegistryByteBuf, V> packetCodec) {
			this.packetCodec = packetCodec;
			return this;
		}

		public Builder<R, V> existingCombined(Function<R, V> existingGetter,
											  Supplier<Iterator<Entry<R, V>>> existingIterator) {
			this.existingGetter = existingGetter;
			this.existingIterator = existingIterator;
			return this;
		}

		/**
		 * Creates a new metatag and registers it.
		 */
		public Metatag<R, V> build() {
			Metatag<R, V> metatag = existingGetter != null && existingIterator != null ?
					new ExistingCombinedMetatag<>(registry, id, codec, packetCodec, side, existingGetter, existingIterator) :
					new MetatagImpl<>(registry, id, codec, packetCodec, side);

			if (side == ResourceType.CLIENT_RESOURCES && FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
				SpecterGlobals.LOGGER.warn(
						"Client-side metatag {} is being registered on the server. This should only be done on the client.",
						id
				);

				SpecterGlobals.LOGGER.warn("This warning may be changed to an exception in the future.");
			}

			MetatagHolder.of(registry).specter$registerMetatag(metatag);
			return metatag;
		}
	}
}
