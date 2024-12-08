package dev.spiritstudios.specter.mixin.dfu;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import dev.spiritstudios.specter.impl.dfu.SpecterDataFixerRegistryImpl;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DataFixTypes.class)
public abstract class DataFixTypesMixin {
	@WrapMethod(
		method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/nbt/NbtCompound;II)Lnet/minecraft/nbt/NbtCompound;"
	)
	private NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int oldVersion, int newVersion, Operation<NbtCompound> original) {
		return SpecterDataFixerRegistryImpl.get().update(
			(DataFixTypes) (Object) this,
			original.call(dataFixer, nbt, oldVersion, newVersion)
		);
	}

	@SuppressWarnings("unchecked")
	@WrapMethod(
		method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;"
	)
	private <T> Dynamic<T> update(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion, int newVersion, Operation<Dynamic<T>> original) {
		Dynamic<T> result = original.call(dataFixer, dynamic, oldVersion, newVersion);

		if (!(result.getValue() instanceof NbtCompound compound))
			return result;

		return (Dynamic<T>) new Dynamic<>(NbtOps.INSTANCE, SpecterDataFixerRegistryImpl.get().update(
			(DataFixTypes) (Object) this,
			compound
		));
	}
}
