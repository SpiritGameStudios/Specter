package dev.spiritstudios.specter.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;

import dev.spiritstudios.specter.api.entity.EntityPart;
import dev.spiritstudios.specter.api.entity.PartHolder;
import dev.spiritstudios.specter.impl.entity.EntityPartWorld;

@Mixin(targets = "net/minecraft/server/world/ServerWorld$ServerEntityHandler")
public abstract class ServerWorld$ServerEntityHandlerMixin {
	@Inject(method = "startTracking(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateEventHandler(Ljava/util/function/BiConsumer;)V"))
	private void startTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.parts()) {
				((EntityPartWorld) entity.getWorld()).specter$parts().put(part.getId(), part);
			}
		}
	}

	@Inject(method = "stopTracking(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateEventHandler(Ljava/util/function/BiConsumer;)V"))
	private void stopTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.parts()) {
				((EntityPartWorld) entity.getWorld()).specter$parts().remove(part.getId(), part);
			}
		}
	}
}
