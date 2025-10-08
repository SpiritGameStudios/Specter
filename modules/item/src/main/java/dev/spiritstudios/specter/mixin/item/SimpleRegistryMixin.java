package dev.spiritstudios.specter.mixin.item;


import java.util.Map;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.impl.item.UnfrozenRegistry;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> implements UnfrozenRegistry<T> {
	@Shadow
	@Final
	private Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry;

	@Shadow
	@Final
	private Map<Identifier, RegistryEntry.Reference<T>> idToEntry;

	@Shadow
	@Final
	private Map<T, RegistryEntry.Reference<T>> valueToEntry;

	@Shadow
	@Final
	private ObjectList<RegistryEntry.Reference<T>> rawIdToEntry;

	@Shadow
	@Final
	private Reference2IntMap<T> entryToRawId;

	@Shadow
	@Final
	private Map<RegistryKey<T>, RegistryEntryInfo> keyToEntryInfo;

	@Override
	public void specter$remove(RegistryKey<T> key) {
		RegistryEntry.Reference<T> reference = this.keyToEntry.get(key);
		T value = reference.value();

		this.keyToEntry.remove(key);
		this.idToEntry.remove(key.getValue());
		this.valueToEntry.remove(value);
		this.rawIdToEntry.remove(reference);
		this.entryToRawId.removeInt(value);
		this.keyToEntryInfo.remove(key);
	}
}
