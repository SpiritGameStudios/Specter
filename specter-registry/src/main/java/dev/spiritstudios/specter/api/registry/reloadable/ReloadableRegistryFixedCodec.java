package dev.spiritstudios.specter.api.registry.reloadable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class ReloadableRegistryFixedCodec<E> implements Codec<RegistryEntry<E>> {
	private final RegistryKey<? extends Registry<E>> registry;

	private ReloadableRegistryFixedCodec(RegistryKey<? extends Registry<E>> registry) {
		this.registry = registry;
	}

	public static <E> ReloadableRegistryFixedCodec<E> of(RegistryKey<? extends Registry<E>> registry) {
		return new ReloadableRegistryFixedCodec<>(registry);
	}

	public <T> DataResult<T> encode(RegistryEntry<E> registryEntry, DynamicOps<T> ops, T object) {
		Optional<? extends RegistryEntryOwner<E>> owner =
			SpecterReloadableRegistries.lookup().flatMap(lookup -> lookup.getOptional(this.registry));

		if (owner.isEmpty())
			return DataResult.error(() -> "Can't access registry " + this.registry);

		if (!registryEntry.ownerEquals(owner.get()))
			return DataResult.error(() -> "Element " + registryEntry + " is not valid in current registry set");

		return registryEntry.getKeyOrValue()
			.map(
				registryKey -> Identifier.CODEC.encode(registryKey.getValue(), ops, object),
				value -> DataResult.error(() -> "Elements from registry " + this.registry + " can't be serialized to a value")
			);

	}

	@Override
	public <T> DataResult<Pair<RegistryEntry<E>, T>> decode(DynamicOps<T> ops, T input) {
		Optional<? extends RegistryEntryLookup<E>> wrapper =
			SpecterReloadableRegistries.lookup()
				.flatMap(lookup -> lookup.getOptional(this.registry));

		if (wrapper.isEmpty()) return DataResult.error(() -> "Can't access registry " + this.registry);

		// mojang what in the ever loving fuck is this
		return Identifier.CODEC.decode(ops, input)
			.flatMap(
				pair -> {
					Identifier identifier = pair.getFirst();

					return wrapper.get()
						.getOptional(RegistryKey.of(this.registry, identifier))
						.map(DataResult::<RegistryEntry<E>>success)
						.orElseGet(() -> DataResult.error(() -> "Failed to get element " + identifier))
						.map(value -> Pair.of(value, pair.getSecond()))
						.setLifecycle(Lifecycle.stable());
				}
			);
	}

	public String toString() {
		return "RegistryFixedCodec[" + this.registry + "]";
	}
}
