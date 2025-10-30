package dev.spiritstudios.specter.api.block.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Simple {@link net.minecraft.world.Container} implementation.
 */
public interface ImplementedInventory extends Container {
	static ImplementedInventory of(NonNullList<ItemStack> items) {
		return () -> items;
	}

	static ImplementedInventory ofSize(int size) {
		return of(NonNullList.withSize(size, ItemStack.EMPTY));
	}

	/**
	 * Gets the item list of this inventory.
	 *
	 * @return The item list of this inventory.
	 * @implSpec Must always return the same instance.
	 */
	NonNullList<ItemStack> getItems();

	@Override
	default int getContainerSize() {
		return getItems().size();
	}

	@Override
	default boolean isEmpty() {
		for (int i = 0; i < getContainerSize(); i++)
			if (!getItem(i).isEmpty()) return false;

		return true;
	}

	@Override
	default ItemStack getItem(int slot) {
		return getItems().get(slot);
	}

	@Override
	default ItemStack removeItem(int slot, int amount) {
		ItemStack result = ContainerHelper.removeItem(getItems(), slot, amount);
		if (!result.isEmpty()) setChanged();
		return result;
	}

	@Override
	default ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(getItems(), slot);
	}

	@Override
	default void setItem(int slot, ItemStack stack) {
		getItems().set(slot, stack);
		if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
	}

	@Override
	default void clearContent() {
		getItems().clear();
	}

	@Override
	default void setChanged() {

	}

	@Override
	default boolean stillValid(Player player) {
		return true;
	}
}
