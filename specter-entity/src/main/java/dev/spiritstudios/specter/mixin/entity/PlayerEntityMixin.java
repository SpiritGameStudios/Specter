package dev.spiritstudios.specter.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import dev.spiritstudios.specter.api.entity.EntityPart;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	@ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 1)
	private Entity attack(Entity value) {
		return value instanceof EntityPart<?> part ? part.getOwner() : value;
	}
}
