package dev.spiritstudios.specter.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.mixin.block.BlockAccessor;

/**
 * Utility class for modifying {@link Block} properties.
 *
 * @see BlockState
 */
public final class BlockStatePropertyModifications {
	/**
	 * Adds a property to a block.
	 *
	 * @param block        The block to add the property to
	 * @param property     The property to add
	 * @param defaultValue The default value for the property
	 * @param <T>          The type of the property
	 */
	public static <T extends Comparable<T>> void add(Block block, Property<T> property, T defaultValue) {
		if (block.getStateManager().getProperties().contains(property)) {
			SpecterGlobals.debug("Property " + property.getName() + " already exists for block " + block);
			return;
		}

		StateManager.Builder<Block, BlockState> stateBuilder = new StateManager.Builder<>(block);
		stateBuilder.add(property);
		block.getStateManager().getProperties().forEach(stateBuilder::add);
		((BlockAccessor) block).setStateManager(stateBuilder.build(Block::getDefaultState, BlockState::new));

		BlockState defaultState = block.getStateManager().getDefaultState();
		if (defaultValue != null) defaultState = defaultState.with(property, defaultValue);
		((BlockAccessor) block).setDefaultState(defaultState);
	}

	/**
	 * Removes a property from a block.
	 *
	 * @param block    The block to remove the property from
	 * @param property The property to remove
	 * @param <T>      The type of the property
	 */
	public static <T extends Comparable<T>> void remove(Block block, Property<T> property) {
		if (!block.getStateManager().getProperties().contains(property)) {
			SpecterGlobals.debug("Property " + property.getName() + " does not exist for block " + block);
			return;
		}

		StateManager.Builder<Block, BlockState> stateBuilder = new StateManager.Builder<>(block);
		block.getStateManager().getProperties().stream().filter(p -> p != property).forEach(stateBuilder::add);
		((BlockAccessor) block).setStateManager(stateBuilder.build(Block::getDefaultState, BlockState::new));

		BlockState defaultState = block.getStateManager().getDefaultState();
		((BlockAccessor) block).setDefaultState(defaultState);
	}

	/**
	 * Sets the default value for a property of a block.
	 *
	 * @param block        The block to set the default value for
	 * @param property     The property to set the default value for
	 * @param defaultValue The default value for the property
	 * @param <T>          The type of the property
	 */
	public static <T extends Comparable<T>> void setDefault(Block block, Property<T> property, T defaultValue) {
		if (!block.getStateManager().getProperties().contains(property))
			throw new IllegalArgumentException("Property " + property.getName() + " does not exist for block " + block);

		BlockState defaultState = block.getStateManager().getDefaultState().with(property, defaultValue);
		((BlockAccessor) block).setDefaultState(defaultState);
	}
}
