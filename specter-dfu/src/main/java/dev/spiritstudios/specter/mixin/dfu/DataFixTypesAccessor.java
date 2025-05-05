package dev.spiritstudios.specter.mixin.dfu;

import com.mojang.datafixers.DSL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.datafixer.DataFixTypes;

@Mixin(DataFixTypes.class)
public interface DataFixTypesAccessor {
	@Accessor
	DSL.TypeReference getTypeReference();
}
