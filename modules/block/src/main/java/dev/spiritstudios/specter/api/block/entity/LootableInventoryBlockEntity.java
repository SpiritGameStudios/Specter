package dev.spiritstudios.specter.api.block.entity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

/**
 * Very similar to {@link net.minecraft.block.entity.LootableContainerBlockEntity}, but for blocks without a menu.
 */
public class LootableInventoryBlockEntity extends BlockEntity implements ImplementedInventory, LootableInventory {
	protected final DefaultedList<ItemStack> inventory;

	@Nullable
	protected RegistryKey<LootTable> lootTable;
	protected long lootTableSeed = 0L;

	public LootableInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
		super(type, pos, state);
		inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	@Nullable
	public RegistryKey<LootTable> getLootTable() {
		return this.lootTable;
	}

	public void setLootTable(@Nullable RegistryKey<LootTable> lootTable) {
		this.lootTable = lootTable;
	}

	public long getLootTableSeed() {
		return this.lootTableSeed;
	}

	public void setLootTableSeed(long lootTableSeed) {
		this.lootTableSeed = lootTableSeed;
	}


	public boolean isEmpty() {
		this.generateLoot(null);
		return ImplementedInventory.super.isEmpty();
	}

	public ItemStack getStack(int slot) {
		this.generateLoot(null);
		return ImplementedInventory.super.getStack(slot);
	}

	public ItemStack removeStack(int slot, int amount) {
		this.generateLoot(null);
		return ImplementedInventory.super.removeStack(slot, amount);
	}

	public ItemStack removeStack(int slot) {
		this.generateLoot(null);
		return ImplementedInventory.super.removeStack(slot);
	}

	public void setStack(int slot, ItemStack itemStack) {
		this.generateLoot(null);
		ImplementedInventory.super.setStack(slot, itemStack);
	}


	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);

		if (!this.writeLootTable(view)) Inventories.writeData(view, this.inventory);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);

		this.clear();

		if (!this.readLootTable(view)) Inventories.readData(view, this.inventory);
	}

	@Override
	protected void readComponents(ComponentsAccess components) {
		super.readComponents(components);
		ContainerLootComponent containerLootComponent = components.get(DataComponentTypes.CONTAINER_LOOT);
		if (containerLootComponent != null) {
			this.lootTable = containerLootComponent.lootTable();
			this.lootTableSeed = containerLootComponent.seed();
		}
	}

	@Override
	protected void addComponents(ComponentMap.Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		if (this.lootTable != null)
			componentMapBuilder.add(DataComponentTypes.CONTAINER_LOOT, new ContainerLootComponent(this.lootTable, this.lootTableSeed));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeFromCopiedStackData(WriteView view) {
		super.removeFromCopiedStackData(view);
		view.remove("LootTable");
		view.remove("LootTableSeed");
	}
}
