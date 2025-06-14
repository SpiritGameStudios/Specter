package dev.spiritstudios.specter.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends WorldMixin {

	@ModifyReturnValue(method = "getEntityOrDragonPart", at = @At("RETURN"))
	private Entity getEntityOrDragonPart(Entity original, @Local(argsOnly = true) int id) {
		return original != null ? original : this.specter$parts.get(id);
	}
}
