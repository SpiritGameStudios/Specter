package dev.spiritstudios.specter.mixin.item;


import java.util.Map;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import dev.spiritstudios.specter.impl.item.UnfrozenRegistry;

@Mixin(MappedRegistry.class)
public abstract class SimpleRegistryMixin<T> implements UnfrozenRegistry<T> {
	@Shadow
	@Final
	private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

	@Shadow
	@Final
	private Map<ResourceLocation, Holder.Reference<T>> byLocation;

	@Shadow
	@Final
	private Map<T, Holder.Reference<T>> byValue;

	@Shadow
	@Final
	private ObjectList<Holder.Reference<T>> byId;

	@Shadow
	@Final
	private Reference2IntMap<T> toId;

	@Shadow
	@Final
	private Map<ResourceKey<T>, RegistrationInfo> registrationInfos;

	@Override
	public void specter$remove(ResourceKey<T> key) {
		Holder.Reference<T> reference = this.byKey.get(key);
		T value = reference.value();

		this.byKey.remove(key);
		this.byLocation.remove(key.location());
		this.byValue.remove(value);
		this.byId.remove(reference);
		this.toId.removeInt(value);
		this.registrationInfos.remove(key);
	}
}
