package dev.spiritstudios.specter.mixin.debug;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SharedConstants.class)
public abstract class SharedConstantsMixin {
	@Shadow
	public static boolean isDevelopment;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void clinit(CallbackInfo ci) {
		isDevelopment = true;
	}
}
