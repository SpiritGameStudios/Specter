package dev.spiritstudios.specter.api.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

/**
 * Simple {@link net.minecraft.inventory.Inventory} implementation.
 */
public interface ImplementedInventory extends Inventory {
	/**
	 * Gets the item list of this inventory.
	 * Must always return the same instance.
	 *
	 * @return The item list of this inventory.
	 */
	DefaultedList<ItemStack> getItems();

	static ImplementedInventory of(DefaultedList<ItemStack> items) {
		return () -> items;
	}

	static ImplementedInventory ofSize(int size) {
		return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
	}

	@Override
	default int size() {
		return getItems().size();
	}

	@Override
	default boolean isEmpty() {
		for (int i = 0; i < size(); i++)
			if (!getStack(i).isEmpty()) return false;

		return true;
	}

	@Override
	default ItemStack getStack(int slot) {
		return getItems().get(slot);
	}

	@Override
	default ItemStack removeStack(int slot, int amount) {
		ItemStack result = Inventories.splitStack(getItems(), slot, amount);
		if (!result.isEmpty()) markDirty();
		return result;
	}

	@Override
	default ItemStack removeStack(int slot) {
		return Inventories.removeStack(getItems(), slot);
	}

	@Override
	default void setStack(int slot, ItemStack stack) {
		getItems().set(slot, stack);
		if (stack.getCount() > getMaxCountPerStack()) stack.setCount(getMaxCountPerStack());
	}

	@Override
	default void clear() {
		getItems().clear();
	}

	@Override
	default void markDirty() {

	}

	@Override
	default boolean canPlayerUse(PlayerEntity player) {
		return true;
	}
}
