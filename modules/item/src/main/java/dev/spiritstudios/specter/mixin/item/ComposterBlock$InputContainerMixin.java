package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.level.ItemLike;

import dev.spiritstudios.specter.api.item.ItemMetatags;

@Mixin(targets = "net.minecraft.world.level.block.ComposterBlock$InputContainer")
public abstract class ComposterBlock$InputContainerMixin {
	@WrapOperation(method = "canPlaceItemThroughFace", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z", remap = false))
	private boolean canInsert(Object2FloatMap<ItemLike> instance, Object o, Operation<Boolean> original) {
		return ItemMetatags.COMPOSTING_CHANCE.containsKey(((ItemLike) o).asItem()) || original.call(instance, o);
	}
}
