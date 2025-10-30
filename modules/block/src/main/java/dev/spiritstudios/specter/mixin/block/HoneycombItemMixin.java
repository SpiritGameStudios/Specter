package dev.spiritstudios.specter.mixin.block;

import java.util.Optional;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import dev.spiritstudios.specter.api.block.BlockMetatags;


@Mixin(HoneycombItem.class)
public abstract class HoneycombItemMixin {
	@WrapOperation(method = "getWaxed", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private static Optional<Block> getWaxedState(
			Object value,
			Operation<Optional<Block>> original,
			@Local(argsOnly = true) BlockState state
	) {
		return BlockMetatags.WAXABLE.get(state.getBlock())
				.or(() -> original.call(value));
	}
}
