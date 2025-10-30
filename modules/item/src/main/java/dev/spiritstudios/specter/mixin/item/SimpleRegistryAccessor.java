package dev.spiritstudios.specter.mixin.item;

import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MappedRegistry.class)
public interface SimpleRegistryAccessor {
	@Accessor
	boolean getFrozen();

	@Accessor
	void setFrozen(boolean value);
}
