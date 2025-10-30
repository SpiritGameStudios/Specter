package dev.spiritstudios.specter.mixin.dfu;

import com.mojang.datafixers.DSL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.datafix.DataFixTypes;

@Mixin(DataFixTypes.class)
public interface DataFixTypesAccessor {
	@Accessor
	DSL.TypeReference getType();
}
