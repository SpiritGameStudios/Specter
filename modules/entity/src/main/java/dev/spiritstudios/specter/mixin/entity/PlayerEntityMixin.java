package dev.spiritstudios.specter.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import dev.spiritstudios.specter.api.entity.EntityPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {

	@ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 1)
	private Entity attack(Entity value) {
		return value instanceof EntityPart<?> part ? part.getOwner() : value;
	}
}
