//package dev.spiritstudios.specter.impl.block;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.state.StateManager;
//import net.minecraft.state.property.Property;
//
//import dev.spiritstudios.specter.api.block.BlockStatePropertyModificationContext;
//import dev.spiritstudios.specter.impl.core.Specter;
//import dev.spiritstudios.specter.mixin.block.BlockAccessor;
//
//public class BlockStatePropertyModificationContextImpl implements BlockStatePropertyModificationContext {
//	private final Block block;
//
//	private final Set<Property<?>> removals = new HashSet<>();
//	private final List<PropertyPair<?>> defaultProperties = new ArrayList<>();
//	private final StateManager.Builder<Block, BlockState> stateBuilder;
//
//	public BlockStatePropertyModificationContextImpl(Block block) {
//		this.block = block;
//		this.stateBuilder = new StateManager.Builder<>(this.block);
//	}
//
//	@Override
//	public <T extends Comparable<T>> void add(Property<T> property, T defaultValue) {
//		if (block.getStateManager().getProperties().contains(property)) {
//			Specter.LOGGER.warn("Property {} already exists for block {}, but something attempted to add it.", property.getName(), block);
//			return;
//		}
//
//		stateBuilder.add(property);
//		defaultProperties.add(new PropertyPair<>(property, defaultValue));
//	}
//
//	@Override
//	public <T extends Comparable<T>> void remove(Property<T> property) {
//		if (!block.getStateManager().getProperties().contains(property)) {
//			Specter.LOGGER.warn("Property {} does not exist for block {}, but something attempted to remove it.", property.getName(), block);
//			return;
//		}
//
//		removals.add(property);
//	}
//
//	@Override
//	public <T extends Comparable<T>> void setDefaultValue(Property<T> property, T defaultValue) {
//		if (!block.getStateManager().getProperties().contains(property))
//			throw new IllegalArgumentException("Property " + property.getName() + " does not exist for block " + block);
//
//		defaultProperties.add(new PropertyPair<>(property, defaultValue));
//	}
//
//	public void done() {
//		block.getStateManager().getProperties()
//				.stream()
//				.filter(p -> !removals.contains(p))
//				.forEach(stateBuilder::add);
//
//		((BlockAccessor) block).setStateManager(stateBuilder.build(Block::getDefaultState, BlockState::new));
//
//		BlockState defaultState = block.getStateManager().getDefaultState();
//
//		for (PropertyPair<?> defaultProperty : defaultProperties) {
//			defaultState = defaultProperty.apply(defaultState);
//		}
//
//		((BlockAccessor) block).setDefaultState(defaultState);
//	}
//
//	private record PropertyPair<T extends Comparable<T>>(Property<T> property, T defaultValue) {
//		public BlockState apply(BlockState state) {
//			return state.with(property, defaultValue);
//		}
//	}
//}
