package dev.spiritstudios.specter.api.registry.reloadable;

import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.IndexedIterable;

import java.util.function.Function;

public final class SpecterRegistryPacketCodecs {
	public static <T> PacketCodec<ByteBuf, T> registryValue(RegistryKey<? extends Registry<T>> key) {
		return registry(key, registry -> registry);
	}

	public static <T> PacketCodec<ByteBuf, RegistryEntry<T>> registryEntry(RegistryKey<? extends Registry<T>> key) {
		return registry(key, Registry::getIndexedEntries);
	}

	private static <T, R> PacketCodec<ByteBuf, R> registry(
		RegistryKey<? extends Registry<T>> key, Function<Registry<T>, IndexedIterable<R>> registryTransformer
	) {
		return new PacketCodec<>() {
			private IndexedIterable<R> getIterable() {
				return registryTransformer.apply(
					SpecterReloadableRegistriesImpl.manager()
						.orElseThrow()
						.getOrThrow(key)
				);
			}

			public R decode(ByteBuf buf) {
				return this.getIterable().getOrThrow(VarInts.read(buf));
			}

			public void encode(ByteBuf buf, R object) {
				VarInts.write(buf, this.getIterable().getRawIdOrThrow(object));
			}
		};
	}

}
