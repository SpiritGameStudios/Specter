package dev.spiritstudios.specter.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;

import dev.spiritstudios.specter.api.entity.EntityPart;

@Mixin(ChunkMap.class)
public abstract class ServerChunkLoadingManagerMixin {

	@WrapMethod(method = "addEntity")
	private void loadEntity(Entity entity, Operation<Void> original) {
		if (entity instanceof EntityPart<?>) return;
		original.call(entity);
	}
}
