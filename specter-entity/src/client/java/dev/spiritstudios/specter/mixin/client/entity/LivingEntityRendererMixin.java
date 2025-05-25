package dev.spiritstudios.specter.mixin.client.entity;

import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import dev.spiritstudios.specter.api.entity.EntityPart;
import dev.spiritstudios.specter.api.entity.PartHolder;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
	@Inject(method = "appendHitboxes(Lnet/minecraft/entity/LivingEntity;Lcom/google/common/collect/ImmutableList$Builder;F)V", at = @At("HEAD"))
	private void appendHitboxes(LivingEntity entity, ImmutableList.Builder<EntityHitbox> builder, float tickProgress, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.parts()) {
				Box box = part.getBoundingBox();
				builder.add(new EntityHitbox(
						box.minX - entity.getX(),
						box.minY - entity.getY(),
						box.minZ - entity.getZ(),
						box.maxX - entity.getX(),
						box.maxY - entity.getY(),
						box.maxZ - entity.getZ(),
						1.0F,
						1.0F,
						1.0F
				));
			}
		}
	}
}
