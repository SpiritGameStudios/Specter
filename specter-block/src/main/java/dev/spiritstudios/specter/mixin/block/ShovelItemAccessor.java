package dev.spiritstudios.specter.mixin.block;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ShovelItem;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;

@Mixin(ShovelItem.class)
public interface ShovelItemAccessor {
	@Accessor("PATH_STATES")
	static Map<Block, BlockState> getPathStates() {
		throw new UnreachableException();
	}
}
