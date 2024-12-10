package dev.spiritstudios.specter.mixin.registry.client;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface WorldAccessor {
	@Mutable
	@Accessor
	void setRegistryManager(DynamicRegistryManager set);
}
