package dev.spiritstudios.specter.mixin.registry.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

@Mixin(Level.class)
public interface WorldAccessor {
	@Mutable
	@Accessor
	void setRegistryAccess(RegistryAccess set);
}
