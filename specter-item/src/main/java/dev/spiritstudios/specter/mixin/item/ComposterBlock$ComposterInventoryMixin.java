package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.spiritstudios.specter.impl.item.SpecterItem;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.block.ComposterBlock$ComposterInventory")
public class ComposterBlock$ComposterInventoryMixin {
	@WrapOperation(method = "canInsert", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z", remap = false))
	private boolean canInsert(Object2FloatMap<ItemConvertible> instance, Object o, Operation<Boolean> original) {
		return SpecterItem.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey((ItemConvertible) o) || original.call(instance, o);
	}
}
