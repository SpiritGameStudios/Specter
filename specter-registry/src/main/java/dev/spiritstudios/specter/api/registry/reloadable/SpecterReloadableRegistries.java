package dev.spiritstudios.specter.api.registry.reloadable;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.core.Specter;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;

/**
 * A reloadable registry is effectively a {@link net.fabricmc.fabric.api.event.registry.DynamicRegistries DynamicRegistry}
 * but as the name would imply, reloadable.
 * <h2>Basic usage</h2>
 * Reloadable registries can be registered using {@link #register(RegistryKey, Codec)}. These registries will not be synced to the client.
 * You can access a reloadable registry via {@link #lookup()}, provided a world is currently loaded.
 * <h2>Synchronization</h2>
 * As stated before, reloadable registries are not synced with the client by default.
 * To register a synced reloadable registry, use {@link #registerSynced(RegistryKey, Codec, PacketCodec)}.
 * Doing this will make your reloadable registry available on the client, still via {@link #lookup()}.
 */
public final class SpecterReloadableRegistries {
	public static <T> void register(RegistryKey<Registry<T>> key, Codec<T> codec) {
		SpecterReloadableRegistriesImpl.register(key, codec);
	}

	public static <T> void registerSynced(RegistryKey<Registry<T>> key, Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
		SpecterReloadableRegistriesImpl.registerSynced(key, codec, packetCodec);
	}

	/**
	 * @return The current reloadable registry manager, or, if no world is currently loaded, {@link Optional#empty()}
	 */
	public static Optional<RegistryWrapper.WrapperLookup> lookup() {
		Optional<RegistryWrapper.WrapperLookup> lookup = SpecterReloadableRegistriesImpl.manager()
				.map(manager -> manager); // i love java
		if (lookup.isEmpty() && SpecterGlobals.DEBUG)
			Specter.LOGGER.warn("Accessed reloadable registry lookup while it is unset. This may be a bug.");
		return lookup;
	}
}
