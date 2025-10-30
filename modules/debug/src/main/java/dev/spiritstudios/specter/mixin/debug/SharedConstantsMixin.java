package dev.spiritstudios.specter.mixin.debug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.SharedConstants;

@Mixin(SharedConstants.class)
public abstract class SharedConstantsMixin {

	@Shadow
	public static boolean IS_RUNNING_IN_IDE;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void clinit(CallbackInfo ci) {
		IS_RUNNING_IN_IDE = true;
	}
}
