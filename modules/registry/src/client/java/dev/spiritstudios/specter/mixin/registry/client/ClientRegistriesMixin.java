package dev.spiritstudios.specter.mixin.registry.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.network.ClientRegistries;
import net.minecraft.registry.DynamicRegistryManager;

import dev.spiritstudios.specter.impl.registry.client.MutableRegistryManager;

@Mixin(ClientRegistries.class)
public abstract class ClientRegistriesMixin {
	// I could see this causing incompatibility in the future, keep an eye on this
	@WrapOperation(method = "createRegistryManager(Lnet/minecraft/resource/ResourceFactory;Lnet/minecraft/registry/DynamicRegistryManager$Immutable;Z)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/DynamicRegistryManager;toImmutable()Lnet/minecraft/registry/DynamicRegistryManager$Immutable;"))
	private DynamicRegistryManager.Immutable makeMutable(DynamicRegistryManager instance, Operation<DynamicRegistryManager.Immutable> original) {
		return new MutableRegistryManager(original.call(instance).streamAllRegistries());
	}
}
