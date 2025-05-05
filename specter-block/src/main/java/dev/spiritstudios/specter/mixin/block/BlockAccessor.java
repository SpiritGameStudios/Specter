package dev.spiritstudios.specter.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

@Mixin(Block.class)
public interface BlockAccessor {
	@Mutable
	@Accessor
	void setStateManager(StateManager<Block, BlockState> stateManager);

	@Accessor
	void setDefaultState(BlockState defaultState);
}
