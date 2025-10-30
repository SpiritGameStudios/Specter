package dev.spiritstudios.specter.mixin.client.entity;


import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

import dev.spiritstudios.specter.api.entity.EntityPart;
import dev.spiritstudios.specter.api.entity.PartHolder;

@Mixin(targets = "net/minecraft/client/multiplayer/ClientLevel$EntityCallbacks")
public abstract class ClientWorld$ClientEntityHandlerMixin {

	@Shadow
	@Final
	ClientLevel field_27735;

	@Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
	private void startTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getSpecterEntityParts()) {
				this.field_27735.specter$getParts().put(part.getId(), part);
			}
		}
	}

	@Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
	private void stopTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getSpecterEntityParts()) {
				this.field_27735.specter$getParts().remove(part.getId(), part);
			}
		}
	}
}
