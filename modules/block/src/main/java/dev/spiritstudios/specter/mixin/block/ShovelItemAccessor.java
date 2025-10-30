package dev.spiritstudios.specter.mixin.block;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;

@Mixin(ShovelItem.class)
public interface ShovelItemAccessor {
	@Accessor
	static Map<Block, BlockState> getFLATTENABLES() {
		throw new UnreachableException();
	}
}
