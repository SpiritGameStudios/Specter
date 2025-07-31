package dev.spiritstudios.specter.mixin.item;

import net.minecraft.registry.SimpleRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleRegistry.class)
public interface SimpleRegistryAccessor {
	@Accessor
	boolean getFrozen();

	@Accessor
	void setFrozen(boolean value);
}
