package dev.spiritstudios.specter.mixin.dfu;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixer.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DataFixTypes.class)
public interface DataFixTypesAccessor {
	@Accessor
	DSL.TypeReference getTypeReference();
}
