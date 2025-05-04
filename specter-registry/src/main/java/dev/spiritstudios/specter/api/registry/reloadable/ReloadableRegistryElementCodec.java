package dev.spiritstudios.specter.api.registry.reloadable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class ReloadableRegistryElementCodec<E> implements Codec<RegistryEntry<E>> {
	private final RegistryKey<? extends Registry<E>> registryRef;
	private final Codec<E> elementCodec;
	private final boolean allowInlineDefinitions;

	private ReloadableRegistryElementCodec(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
		this.registryRef = registryRef;
		this.elementCodec = elementCodec;
		this.allowInlineDefinitions = allowInlineDefinitions;
	}

	public static <E> ReloadableRegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
		return of(registryRef, elementCodec, true);
	}

	public static <E> ReloadableRegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
		return new ReloadableRegistryElementCodec<>(registryRef, elementCodec, allowInlineDefinitions);
	}

	public <T> DataResult<T> encode(RegistryEntry<E> registryEntry, DynamicOps<T> ops, T object) {
		Optional<? extends RegistryEntryOwner<E>> owner =
			SpecterReloadableRegistries.lookup().flatMap(lookup -> lookup.getOptional(this.registryRef));

		if (owner.isEmpty()) return this.elementCodec.encode(registryEntry.value(), ops, object);

		if (!registryEntry.ownerEquals(owner.get()))
			return DataResult.error(() -> "Element " + registryEntry + " is not valid in current registry set");

		return registryEntry.getKeyOrValue()
			.map(
				key ->
					Identifier.CODEC.encode(key.getValue(), ops, object),
				value -> this.elementCodec.encode(value, ops, object)
			);
	}

	@Override
	public <T> DataResult<Pair<RegistryEntry<E>, T>> decode(DynamicOps<T> ops, T input) {
		Optional<? extends RegistryWrapper.Impl<E>> wrapper =
			SpecterReloadableRegistries.lookup()
				.flatMap(lookup -> lookup.getOptional(this.registryRef));

		if (wrapper.isEmpty()) return DataResult.error(() -> "Registry does not exist: " + this.registryRef);

		RegistryEntryLookup<E> registryEntryLookup = wrapper.get();
		DataResult<Pair<Identifier, T>> dataResult = Identifier.CODEC.decode(ops, input);
		if (dataResult.result().isEmpty()) {
			return !this.allowInlineDefinitions
				? DataResult.error(() -> "Inline definitions not allowed here")
				: this.elementCodec.decode(ops, input).map(pairx -> pairx.mapFirst(RegistryEntry::of));
		}

		Pair<Identifier, T> pair = dataResult.result().get();
		RegistryKey<E> registryKey = RegistryKey.of(this.registryRef, pair.getFirst());
		return registryEntryLookup.getOptional(registryKey)
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Failed to get element " + registryKey))
			.<Pair<RegistryEntry<E>, T>>map(reference -> Pair.of(reference, pair.getSecond()))
			.setLifecycle(Lifecycle.stable());
	}

	public String toString() {
		return "ReloadableRegistryElementCodec[" + this.registryRef + " " + this.elementCodec + "]";
	}
}
