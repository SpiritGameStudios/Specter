package dev.spiritstudios.specter.api.registry.reloadable;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public final class SpecterReloadableRegistries {
	public static <T> void register(RegistryKey<Registry<T>> key, Codec<T> codec) {
		SpecterReloadableRegistriesImpl.register(key, codec);
	}

	public static <T> void registerSynced(RegistryKey<Registry<T>> key, Codec<T> codec, PacketCodec<ByteBuf, T> packetCodec) {
		SpecterReloadableRegistriesImpl.registerSynced(key, codec, packetCodec);
	}
}
