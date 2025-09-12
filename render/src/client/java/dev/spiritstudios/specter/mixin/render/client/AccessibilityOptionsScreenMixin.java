package dev.spiritstudios.specter.mixin.render.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.option.SimpleOption;

import dev.spiritstudios.specter.impl.render.client.SpecterRenderClient;

@Mixin(AccessibilityOptionsScreen.class)
public abstract class AccessibilityOptionsScreenMixin {
	@ModifyReturnValue(method = "getOptions", at = @At("RETURN"))
	private static SimpleOption<?>[] addOptions(SimpleOption<?>[] original) {
		return ArrayUtils.add(original, SpecterRenderClient.SCREENSHAKE_INTENSITY);
	}
}
