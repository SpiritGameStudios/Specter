package dev.spiritstudios.specter.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends WorldMixin {

	@ModifyReturnValue(method = "getEntityOrPart", at = @At("RETURN"))
	private Entity getEntityOrPart(Entity original, @Local(argsOnly = true) int id) {
		return original != null ? original : this.specter$parts.get(id);
	}
}
