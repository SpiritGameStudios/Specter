package dev.spiritstudios.specter.mixin.dfu;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.spiritstudios.specter.impl.dfu.SpecterDataFixerRegistryImpl;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NbtHelper.class)
public abstract class NbtHelperMixin {
	@ModifyReturnValue(method = "putDataVersion(Lnet/minecraft/nbt/NbtCompound;I)Lnet/minecraft/nbt/NbtCompound;", at = @At("RETURN"))
	private static NbtCompound putDataVersion(NbtCompound original) {
		return SpecterDataFixerRegistryImpl.get().writeDataVersions(original);
	}
}
