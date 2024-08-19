package dev.spiritstudios.specter.mixin.block;

import com.google.common.collect.BiMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.spiritstudios.specter.impl.block.SpecterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Oxidizable.class)
public interface OxidizableMixin {
	@Inject(method = "getIncreasedOxidationBlock", at = @At("HEAD"), cancellable = true)
	private static void getIncreasedOxidationBlock(Block block, CallbackInfoReturnable<Optional<Block>> cir) {
		Optional<Block> increasedOxidationBlock = Optional.ofNullable(SpecterBlock.OXIDATION_LEVEL_INCREASES.get(block));
		if (increasedOxidationBlock.isPresent()) cir.setReturnValue(increasedOxidationBlock);
	}

	@Inject(method = "getDecreasedOxidationBlock", at = @At("HEAD"), cancellable = true)
	private static void getDecreasedOxidationBlock(Block block, CallbackInfoReturnable<Optional<Block>> cir) {
		Optional<Block> decreasedOxidationBlock = Optional.ofNullable(SpecterBlock.OXIDATION_LEVEL_DECREASES.get(block));
		if (decreasedOxidationBlock.isPresent()) cir.setReturnValue(decreasedOxidationBlock);
	}

	@SuppressWarnings("rawtypes")
	@WrapOperation(method = "getUnaffectedOxidationBlock", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/BiMap;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private static Object getUnaffectedOxidationBlock(BiMap instance, Object o, Operation<Object> original) {
		Block block = (Block) o;
		Optional<Block> unaffectedOxidationBlock = Optional.ofNullable(SpecterBlock.OXIDATION_LEVEL_DECREASES.get(block));
		return unaffectedOxidationBlock.orElseGet(() -> (Block) original.call(instance, o));
	}
}

