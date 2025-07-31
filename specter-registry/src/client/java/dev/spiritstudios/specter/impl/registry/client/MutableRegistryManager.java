package dev.spiritstudios.specter.impl.registry.client;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import dev.spiritstudios.specter.api.core.collect.SpecterCollectors;

// mmm yessssss very immutable
public class MutableRegistryManager implements DynamicRegistryManager.Immutable {
	private final Map<RegistryKey<? extends Registry<?>>, Registry<?>> registries;

	public MutableRegistryManager(List<? extends Registry<?>> registries) {
		this.registries = registries.stream().collect(SpecterCollectors.toMap(
				Registry::getKey, registry -> registry,
				Object2ObjectOpenHashMap::new
		));
	}

	public MutableRegistryManager(Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
		this.registries = new Object2ObjectOpenHashMap<>(registries);
	}

	public MutableRegistryManager(Stream<Entry<?>> entryStream) {
		this.registries = entryStream.collect(SpecterCollectors.toMap(
				Entry::key, Entry::value,
				Object2ObjectOpenHashMap::new
		));
	}

	@SuppressWarnings("unchecked")
	private static <T> DynamicRegistryManager.Entry<T> of(RegistryKey<? extends Registry<?>> key, Registry<?> value) {
		return new DynamicRegistryManager.Entry<>((RegistryKey<? extends Registry<T>>) key, (Registry<T>) value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Optional<Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> registryRef) {
		return Optional.ofNullable(this.registries.get(registryRef))
				.map(registry -> (Registry<E>) registry);
	}

	@Override
	public Stream<DynamicRegistryManager.Entry<?>> streamAllRegistries() {
		return this.registries.entrySet().stream().map(entry -> of(entry.getKey(), entry.getValue()));
	}

	public <T extends Registry<?>> void addRegistry(T registry) {
		registries.put(registry.getKey(), registry);
	}
}
