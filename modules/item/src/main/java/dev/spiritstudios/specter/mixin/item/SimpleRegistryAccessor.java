package dev.spiritstudios.specter.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.registry.SimpleRegistry;

@Mixin(SimpleRegistry.class)
public interface SimpleRegistryAccessor {
	@Accessor
	boolean getFrozen();

	@Accessor
	void setFrozen(boolean value);
}
