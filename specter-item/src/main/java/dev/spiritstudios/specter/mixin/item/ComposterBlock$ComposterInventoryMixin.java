package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.ItemConvertible;

import dev.spiritstudios.specter.api.item.ItemMetatags;

@Mixin(targets = "net.minecraft.block.ComposterBlock$ComposterInventory")
public abstract class ComposterBlock$ComposterInventoryMixin {
	@WrapOperation(method = "canInsert", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z", remap = false))
	private boolean canInsert(Object2FloatMap<ItemConvertible> instance, Object o, Operation<Boolean> original) {
		return ItemMetatags.COMPOSTING_CHANCE.containsKey(((ItemConvertible) o).asItem()) || original.call(instance, o);
	}
}
