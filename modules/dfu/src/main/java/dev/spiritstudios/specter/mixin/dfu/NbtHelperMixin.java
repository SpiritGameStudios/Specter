package dev.spiritstudios.specter.mixin.dfu;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

import dev.spiritstudios.specter.impl.dfu.SpecterDataFixerRegistryImpl;

@Mixin(NbtUtils.class)
public abstract class NbtHelperMixin {
	@ModifyReturnValue(method = "addCurrentDataVersion(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
	private static CompoundTag addCurrentDataVersion(CompoundTag original) {
		return SpecterDataFixerRegistryImpl.get().addCurrentDataVersion(original);
	}
}
