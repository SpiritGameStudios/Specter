package dev.spiritstudios.specter.impl.registry.reloadable;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SpecterReloadableRegistriesImpl {
	private static final List<ReloadableRegistryInfo<?>> RELOADABLE_REGISTRIES = new ObjectArrayList<>();
	private static final Map<RegistryKey<Registry<Object>>, PacketCodec<RegistryByteBuf, ?>> SYNCING_CODECS = new Object2ObjectOpenHashMap<>();
	private static DynamicRegistryManager.Immutable reloadableManager;

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

	public static Optional<DynamicRegistryManager.Immutable> registryManager() {
		return Optional.ofNullable(reloadableManager);
	}

	public static void setRegistryManager(DynamicRegistryManager.Immutable manager) {
		reloadableManager = manager;
	}

	public static Map<RegistryKey<Registry<Object>>, PacketCodec<RegistryByteBuf, ?>> syncingCodecs() {
		return SYNCING_CODECS;
	}

	public record ReloadableRegistryInfo<T>(RegistryKey<Registry<T>> key, Codec<T> codec) {
	}
}
