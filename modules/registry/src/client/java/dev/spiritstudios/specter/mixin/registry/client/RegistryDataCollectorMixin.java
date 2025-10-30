package dev.spiritstudios.specter.mixin.registry.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.multiplayer.RegistryDataCollector;
import net.minecraft.core.RegistryAccess;

import dev.spiritstudios.specter.impl.registry.client.MutableRegistryManager;

@Mixin(RegistryDataCollector.class)
public abstract class RegistryDataCollectorMixin {
	// I could see this causing incompatibility in the future, keep an eye on this
	@WrapOperation(method = "loadNewElementsAndTags", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/RegistryAccess$Frozen;freeze()Lnet/minecraft/core/RegistryAccess$Frozen;"))
	private RegistryAccess.Frozen makeMutable(RegistryAccess.Frozen instance, Operation<RegistryAccess.Frozen> original) {
		return new MutableRegistryManager(original.call(instance).registries());
	}
}
