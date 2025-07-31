package dev.spiritstudios.specter.mixin.render.client;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.option.GameOptions;

import dev.spiritstudios.specter.impl.render.client.SpecterRenderClient;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
	@Inject(method = "accept", at = @At("TAIL"))
	private void addScreenshakeIntensity(GameOptions.Visitor visitor, CallbackInfo ci) {
		visitor.accept("specter_screenshakeIntensity", SpecterRenderClient.SCREENSHAKE_INTENSITY);
	}
}
