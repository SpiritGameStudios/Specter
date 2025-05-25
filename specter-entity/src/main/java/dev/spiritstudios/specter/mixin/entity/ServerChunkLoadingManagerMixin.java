package dev.spiritstudios.specter.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerChunkLoadingManager;

import dev.spiritstudios.specter.api.entity.EntityPart;

@Mixin(ServerChunkLoadingManager.class)
public abstract class ServerChunkLoadingManagerMixin {
	@WrapMethod(method = "loadEntity")
	private void loadEntity(Entity entity, Operation<Void> original) {
		if (entity instanceof EntityPart<?>) return;
		original.call(entity);
	}
}
