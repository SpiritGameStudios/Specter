package dev.spiritstudios.specter.mixin.render.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import dev.spiritstudios.specter.impl.render.client.ScreenshakeManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.RandomSource;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	@Final
	private RandomSource random;

	@ModifyExpressionValue(method = "renderWorld", at = @At(value = "NEW", target = "net/minecraft/client/util/math/MatrixStack"))
	private PoseStack applyScreenshake(PoseStack original, @Local(argsOnly = true) DeltaTracker renderTickCounter) {
		ScreenshakeManager.update(renderTickCounter.getGameTimeDeltaPartialTick(true), original, random);
		return original;
	}
}
