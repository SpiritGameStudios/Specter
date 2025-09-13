package dev.spiritstudios.specter.mixin.registry.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;

@Mixin(World.class)
public interface WorldAccessor {
	@Mutable
	@Accessor
	void setRegistryManager(DynamicRegistryManager set);
}
