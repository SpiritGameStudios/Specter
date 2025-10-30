package dev.spiritstudios.specter.impl.registry.client;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import dev.spiritstudios.specter.api.core.collect.SpecterCollectors;

// mmm yessssss very immutable
public class MutableRegistryManager implements RegistryAccess.Frozen {
	private final Map<ResourceKey<? extends Registry<?>>, Registry<?>> registries;

	public MutableRegistryManager(List<? extends Registry<?>> registries) {
		this.registries = registries.stream().collect(SpecterCollectors.toMap(
				Registry::key, registry -> registry,
				Object2ObjectOpenHashMap::new
		));
	}

	public MutableRegistryManager(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
		this.registries = new Object2ObjectOpenHashMap<>(registries);
	}

	public MutableRegistryManager(Stream<RegistryEntry<?>> entryStream) {
		this.registries = entryStream.collect(SpecterCollectors.toMap(
				RegistryEntry::key, RegistryEntry::value,
				Object2ObjectOpenHashMap::new
		));
	}

	@SuppressWarnings("unchecked")
	private static <T> RegistryAccess.RegistryEntry<T> of(ResourceKey<? extends Registry<?>> key, Registry<?> value) {
		return new RegistryAccess.RegistryEntry<>((ResourceKey<? extends Registry<T>>) key, (Registry<T>) value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> @NotNull Optional<Registry<E>> lookup(ResourceKey<? extends Registry<? extends E>> registryRef) {
		return Optional.ofNullable(this.registries.get(registryRef))
				.map(registry -> (Registry<E>) registry);
	}

	@Override
	public @NotNull Stream<RegistryAccess.RegistryEntry<?>> registries() {
		return this.registries.entrySet().stream().map(entry -> of(entry.getKey(), entry.getValue()));
	}

	public <T extends Registry<?>> void addRegistry(T registry) {
		registries.put(registry.key(), registry);
	}
}
