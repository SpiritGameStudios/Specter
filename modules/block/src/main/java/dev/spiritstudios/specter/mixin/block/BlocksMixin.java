//package dev.spiritstudios.specter.mixin.block;
//
//import java.util.function.Consumer;
//
//import com.llamalad7.mixinextras.sugar.Local;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.Blocks;
//
//import dev.spiritstudios.specter.api.block.BlockStatePropertyModificationContext;
//import dev.spiritstudios.specter.impl.block.BlockStatePropertyModificationContextImpl;
//import dev.spiritstudios.specter.impl.block.BlockStatePropertyModificationsImpl;
//
//@Mixin(Blocks.class)
//public abstract class BlocksMixin {
//	@Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getStateManager()Lnet/minecraft/state/StateManager;"))
//	private static void addBlock(CallbackInfo ci, @Local Block block) {
//		Consumer<BlockStatePropertyModificationContext> modifier = BlockStatePropertyModificationsImpl.getModifier(block);
//
//		if (modifier == null) return;
//
//		BlockStatePropertyModificationContextImpl context = new BlockStatePropertyModificationContextImpl(block);
//		modifier.accept(context);
//		context.done();
//	}
//}
