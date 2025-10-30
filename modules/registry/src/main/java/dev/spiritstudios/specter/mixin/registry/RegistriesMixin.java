package dev.spiritstudios.specter.mixin.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.registries.BuiltInRegistries;

import dev.spiritstudios.specter.api.registry.SpecterRegistryEvents;

@Mixin(BuiltInRegistries.class)
public abstract class RegistriesMixin {
	@Inject(method = "freeze", at = @At("TAIL"))
	private static void freeze(CallbackInfo ci) {
		SpecterRegistryEvents.REGISTRIES_FROZEN.invoker().onRegistriesFrozen();
	}
}
