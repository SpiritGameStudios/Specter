package dev.spiritstudios.specter.impl.registry.reloadable;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SpecterReloadableRegistriesImpl {
	private static final List<ReloadableRegistryInfo<?>> RELOADABLE_REGISTRIES = new ObjectArrayList<>();
	private static final Map<RegistryKey<Registry<Object>>, PacketCodec<RegistryByteBuf, ?>> SYNCING_CODECS = new Object2ObjectOpenHashMap<>();
	private static RegistryWrapper.WrapperLookup lookup;

	public static <T> void register(RegistryKey<Registry<T>> key, Codec<T> codec) {
		RELOADABLE_REGISTRIES.add(new ReloadableRegistryInfo<>(key, codec));
	}

	public static <T> void registerSynced(RegistryKey<Registry<T>> key, Codec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
		RELOADABLE_REGISTRIES.add(new ReloadableRegistryInfo<>(key, codec));
		SYNCING_CODECS.put(RegistryKey.ofRegistry(key.getValue()), packetCodec);
	}


	public static List<ReloadableRegistryInfo<?>> reloadableRegistries() {
		return RELOADABLE_REGISTRIES;
	}

	public static Optional<RegistryWrapper.WrapperLookup> lookup() {
		return Optional.ofNullable(lookup);
	}

	public static void setLookup(RegistryWrapper.WrapperLookup lookup) {
		SpecterReloadableRegistriesImpl.lookup = lookup;
	}

	public static Map<RegistryKey<Registry<Object>>, PacketCodec<RegistryByteBuf, ?>> syncingCodecs() {
		return SYNCING_CODECS;
	}

	public record ReloadableRegistryInfo<T>(RegistryKey<Registry<T>> key, Codec<T> codec) {
	}
}
