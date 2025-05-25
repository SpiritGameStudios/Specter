package dev.spiritstudios.specter.mixin.client.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;

import dev.spiritstudios.specter.api.entity.EntityPart;
import dev.spiritstudios.specter.api.entity.PartHolder;
import dev.spiritstudios.specter.impl.entity.EntityPartWorld;

@Mixin(targets = "net/minecraft/client/world/ClientWorld$ClientEntityHandler")
public abstract class ClientWorld$ClientEntityHandlerMixin {
	@Inject(method = "startTracking(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
	private void startTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.parts()) {
				((EntityPartWorld) entity.getWorld()).specter$parts().put(part.getId(), part);
			}
		}
	}

	@Inject(method = "stopTracking(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
	private void stopTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.parts()) {
				((EntityPartWorld) entity.getWorld()).specter$parts().remove(part.getId(), part);
			}
		}
	}
}
