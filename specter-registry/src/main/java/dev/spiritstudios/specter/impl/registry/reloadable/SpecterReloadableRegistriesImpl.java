package dev.spiritstudios.specter.impl.registry.reloadable;

import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.core.SpecterGlobals;

public final class SpecterReloadableRegistriesImpl {
	private static final Map<Identifier, ReloadableRegistryInfo<?>> RELOADABLE_REGISTRIES = new Object2ObjectLinkedOpenHashMap<>();

	private static DynamicRegistryManager manager;

	public static <T> void register(RegistryKey<Registry<T>> key, Codec<T> codec) {
		RELOADABLE_REGISTRIES.put(key.getValue(), new ReloadableRegistryInfo<>(key, codec, null));
	}

	public static <T> void registerSynced(RegistryKey<Registry<T>> key, Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
		RELOADABLE_REGISTRIES.put(key.getValue(), new ReloadableRegistryInfo<>(key, codec, packetCodec));
	}

	public static Map<Identifier, ReloadableRegistryInfo<?>> reloadableRegistries() {
		return RELOADABLE_REGISTRIES;
	}

	public static Optional<DynamicRegistryManager> manager() {
		return Optional.ofNullable(manager);
	}

	public static void setManager(DynamicRegistryManager manager) {
		SpecterReloadableRegistriesImpl.manager = manager;

		if (SpecterGlobals.DEBUG && manager != null) {
			manager.streamAllRegistryKeys().forEach(key -> {
				SpecterGlobals.debug(key.getValue().toString());
			});
		}
	}

	public record ReloadableRegistryInfo<T>(
			RegistryKey<Registry<T>> key,
			Codec<T> codec,
			@Nullable PacketCodec<? super RegistryByteBuf, T> packetCodec
	) {
	}
}
