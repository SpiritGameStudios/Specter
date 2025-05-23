package dev.spiritstudios.specter.mixin.core.client;

import com.mojang.text2speech.Narrator;
import com.mojang.text2speech.OperatingSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.spiritstudios.specter.api.core.SpecterGlobals;

@Mixin(Narrator.class)
public interface NarratorMixin {
	@Inject(method = "getNarrator", at = @At("HEAD"), cancellable = true, remap = false)
	private static void getNarrator(CallbackInfoReturnable<Narrator> cir) {
		if (System.getProperty("specter.disable_narrator") != null || (OperatingSystem.get() == OperatingSystem.LINUX) && SpecterGlobals.DEBUG) {
			cir.setReturnValue(
					new Narrator() {
						@Override
						public void say(String s, boolean b, float v) {

						}

						@Override
						public void clear() {
						}

						@Override
						public void destroy() {
						}

						@Override
						public boolean active() {
							return false;
						}
					}
			);
		}
	}
}
