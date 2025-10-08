package dev.spiritstudios.specter.mixin.item.client;

import java.util.Map;
import java.util.Set;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.network.ClientRegistries;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.ResourceFactory;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;
import dev.spiritstudios.specter.api.item.DataItemGroup;
import dev.spiritstudios.specter.api.item.SpecterItemRegistryKeys;
import dev.spiritstudios.specter.impl.item.UnfrozenRegistry;
import dev.spiritstudios.specter.mixin.item.SimpleRegistryAccessor;

@Mixin(ClientRegistries.class)
public abstract class ClientRegistriesMixin {
	@WrapMethod(method = "createRegistryManager(Lnet/minecraft/resource/ResourceFactory;Lnet/minecraft/registry/DynamicRegistryManager$Immutable;Z)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;")
	private DynamicRegistryManager.Immutable makeMutable(ResourceFactory resourceFactory, DynamicRegistryManager.Immutable registryManager, boolean local, Operation<DynamicRegistryManager.Immutable> original) {
		DynamicRegistryManager.Immutable result = original.call(resourceFactory, registryManager, local);

		if (!(Registries.ITEM_GROUP instanceof SimpleRegistry<ItemGroup> registry)) throw new UnreachableException();

		SimpleRegistryAccessor accessor = (SimpleRegistryAccessor) registry;
		accessor.setFrozen(false);

		@SuppressWarnings("unchecked") UnfrozenRegistry<ItemGroup> unfrozen = ((UnfrozenRegistry<ItemGroup>) registry);

		// Remove data item groups, we are about to load them in again
		// We copy the set since otherwise we would be modifying it while iterating
		for (Map.Entry<RegistryKey<ItemGroup>, ItemGroup> mapEntry : Set.copyOf(registry.getEntrySet())) {
			if (mapEntry.getValue() instanceof DataItemGroup) {
				unfrozen.specter$remove(mapEntry.getKey());
			}
		}

		result.getOrThrow(SpecterItemRegistryKeys.ITEM_GROUP).streamEntries().forEach(entry -> {
			RegistryKey<ItemGroup> key = RegistryKey.of(RegistryKeys.ITEM_GROUP, entry.registryKey().getValue());
			Registry.register(Registries.ITEM_GROUP, key, entry.value());
		});

		accessor.setFrozen(true);

		return result;
	}
}
