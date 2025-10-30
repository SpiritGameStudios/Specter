package dev.spiritstudios.specter.mixin.render.client;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.spiritstudios.specter.impl.render.client.SpecterRenderClient;
import net.minecraft.client.Options;

@Mixin(Options.class)
public abstract class GameOptionsMixin {
	@Inject(method = "processOptions", at = @At("TAIL"))
	private void addScreenshakeIntensity(Options.FieldAccess accessor, CallbackInfo ci) {
		accessor.process("specter_screenshakeIntensity", SpecterRenderClient.SCREENSHAKE_INTENSITY);
	}
}
