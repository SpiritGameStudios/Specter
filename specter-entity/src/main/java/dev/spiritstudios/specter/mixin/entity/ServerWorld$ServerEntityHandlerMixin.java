package dev.spiritstudios.specter.mixin.entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import dev.spiritstudios.specter.api.entity.EntityPart;
import dev.spiritstudios.specter.api.entity.PartHolder;

@Mixin(targets = "net/minecraft/server/world/ServerWorld$ServerEntityHandler")
public abstract class ServerWorld$ServerEntityHandlerMixin {

	@Shadow
	@Final
	ServerWorld field_26936;

	@Inject(method = "startTracking(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateEventHandler(Ljava/util/function/BiConsumer;)V"))
	private void startTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getEntityParts()) {
				this.field_26936.specter$getParts().put(part.getId(), part);
			}
		}
	}

	@Inject(method = "stopTracking(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateEventHandler(Ljava/util/function/BiConsumer;)V"))
	private void stopTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getEntityParts()) {
				this.field_26936.specter$getParts().remove(part.getId(), part);
			}
		}
	}
}
