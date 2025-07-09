package dev.spiritstudios.specter.mixin.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.registry.Registries;

import dev.spiritstudios.specter.api.registry.SpecterRegistryEvents;

@Mixin(Registries.class)
public abstract class RegistriesMixin {
	@Inject(method = "freezeRegistries", at = @At("TAIL"))
	private static void freezeRegistries(CallbackInfo ci) {
		SpecterRegistryEvents.REGISTRIES_FROZEN.invoker().onRegistriesFrozen();
	}
}
