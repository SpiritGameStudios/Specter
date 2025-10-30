package dev.spiritstudios.specter.mixin.item.client;

import java.util.Map;
import java.util.Set;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.multiplayer.RegistryDataCollector;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.item.CreativeModeTab;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;
import dev.spiritstudios.specter.api.item.DataItemGroup;
import dev.spiritstudios.specter.api.item.SpecterItemRegistryKeys;
import dev.spiritstudios.specter.impl.item.UnfrozenRegistry;
import dev.spiritstudios.specter.mixin.item.SimpleRegistryAccessor;

@Mixin(RegistryDataCollector.class)
public abstract class ClientRegistriesMixin {
	@WrapMethod(method = "collectGameRegistries")
	private RegistryAccess.Frozen makeMutable(ResourceProvider resourceFactory, RegistryAccess.Frozen registryManager, boolean local, Operation<RegistryAccess.Frozen> original) {
		RegistryAccess.Frozen result = original.call(resourceFactory, registryManager, local);

		if (!(BuiltInRegistries.CREATIVE_MODE_TAB instanceof MappedRegistry<CreativeModeTab> registry)) throw new UnreachableException();

		SimpleRegistryAccessor accessor = (SimpleRegistryAccessor) registry;
		accessor.setFrozen(false);

		@SuppressWarnings("unchecked") UnfrozenRegistry<CreativeModeTab> unfrozen = ((UnfrozenRegistry<CreativeModeTab>) registry);

		// Remove data item groups, we are about to load them in again
		// We copy the set since otherwise we would be modifying it while iterating
		for (Map.Entry<ResourceKey<CreativeModeTab>, CreativeModeTab> mapEntry : Set.copyOf(registry.entrySet())) {
			if (mapEntry.getValue() instanceof DataItemGroup) {
				unfrozen.specter$remove(mapEntry.getKey());
			}
		}

		result.lookupOrThrow(SpecterItemRegistryKeys.ITEM_GROUP).listElements().forEach(entry -> {
			ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, entry.key().location());
			Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, entry.value());
		});

		accessor.setFrozen(true);

		return result;
	}
}
