package dev.spiritstudios.specter.api.registry.reloadable;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.Optional;

public final class SpecterReloadableRegistries {
	public static <T> void register(RegistryKey<Registry<T>> key, Codec<T> codec) {
		SpecterReloadableRegistriesImpl.register(key, codec);
	}

	public static <T> void registerSynced(RegistryKey<Registry<T>> key, Codec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
		SpecterReloadableRegistriesImpl.registerSynced(key, codec, packetCodec);
	}

	public static Optional<DynamicRegistryManager.Immutable> reloadableManager() {
		return SpecterReloadableRegistriesImpl.registryManager();
	}
}
