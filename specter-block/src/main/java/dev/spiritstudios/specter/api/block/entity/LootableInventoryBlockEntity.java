package dev.spiritstudios.specter.api.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

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

	public ItemStack getStack(int i) {
		this.generateLoot(null);
		return ImplementedInventory.super.getStack(i);
	}

	public ItemStack removeStack(int i, int j) {
		this.generateLoot(null);
		return ImplementedInventory.super.removeStack(i, j);
	}

	public ItemStack removeStack(int i) {
		this.generateLoot(null);
		return ImplementedInventory.super.removeStack(i);
	}

	public void setStack(int i, ItemStack itemStack) {
		this.generateLoot(null);
		ImplementedInventory.super.setStack(i, itemStack);
	}


	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		if (!this.writeLootTable(nbt)) {
			Inventories.writeNbt(nbt, this.inventory, registryLookup);
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.clear();
		if (!this.readLootTable(nbt)) {
			Inventories.readNbt(nbt, this.inventory, registryLookup);
		}
	}

	@Override
	protected void readComponents(BlockEntity.ComponentsAccess components) {
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
		if (this.lootTable != null) {
			componentMapBuilder.add(DataComponentTypes.CONTAINER_LOOT, new ContainerLootComponent(this.lootTable, this.lootTableSeed));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		super.removeFromCopiedStackNbt(nbt);
		nbt.remove("LootTable");
		nbt.remove("LootTableSeed");
	}
}
