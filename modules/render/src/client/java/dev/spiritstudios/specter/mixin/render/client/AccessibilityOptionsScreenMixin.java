package dev.spiritstudios.specter.mixin.render.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import dev.spiritstudios.specter.impl.render.client.SpecterRenderClient;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;

@Mixin(AccessibilityOptionsScreen.class)
public abstract class AccessibilityOptionsScreenMixin {
	@ModifyReturnValue(method = "getOptions", at = @At("RETURN"))
	private static OptionInstance<?>[] addOptions(OptionInstance<?>[] original) {
		return ArrayUtils.add(original, SpecterRenderClient.SCREENSHAKE_INTENSITY);
	}
}
