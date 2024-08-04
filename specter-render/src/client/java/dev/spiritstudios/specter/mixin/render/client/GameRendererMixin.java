package dev.spiritstudios.specter.mixin.render.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.api.render.shake.ScreenshakeManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@ModifyExpressionValue(method = "renderWorld", at = @At(value = "NEW", target = "net/minecraft/client/util/math/MatrixStack"))
	private MatrixStack applyScreenshake(MatrixStack original, @Local(argsOnly = true) RenderTickCounter renderTickCounter) {
		ScreenshakeManager.update(renderTickCounter.getTickDelta(true), original);
		return original;
	}
}
