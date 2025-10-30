package dev.spiritstudios.specter.api.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

/**
 * Very similar to {@link net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity}, but for blocks without a menu.
 */
public class LootableInventoryBlockEntity extends BlockEntity implements ImplementedInventory, RandomizableContainer {
	protected final NonNullList<ItemStack> inventory;

	@Nullable
	protected ResourceKey<LootTable> lootTable;
	protected long lootTableSeed = 0L;

	public LootableInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
		super(type, pos, state);
		inventory = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	@Nullable
	public ResourceKey<LootTable> getLootTable() {
		return this.lootTable;
	}

	public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
		this.lootTable = lootTable;
	}

	public long getLootTableSeed() {
		return this.lootTableSeed;
	}

	public void setLootTableSeed(long lootTableSeed) {
		this.lootTableSeed = lootTableSeed;
	}


	public boolean isEmpty() {
		this.unpackLootTable(null);
		return ImplementedInventory.super.isEmpty();
	}

	public ItemStack getItem(int slot) {
		this.unpackLootTable(null);
		return ImplementedInventory.super.getItem(slot);
	}

	public ItemStack removeItem(int slot, int amount) {
		this.unpackLootTable(null);
		return ImplementedInventory.super.removeItem(slot, amount);
	}

	public ItemStack removeItemNoUpdate(int slot) {
		this.unpackLootTable(null);
		return ImplementedInventory.super.removeItemNoUpdate(slot);
	}

	public void setItem(int slot, ItemStack itemStack) {
		this.unpackLootTable(null);
		ImplementedInventory.super.setItem(slot, itemStack);
	}


	@Override
	public NonNullList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	protected void saveAdditional(ValueOutput view) {
		super.saveAdditional(view);

		if (!this.trySaveLootTable(view)) ContainerHelper.saveAllItems(view, this.inventory);
	}

	@Override
	protected void loadAdditional(ValueInput view) {
		super.loadAdditional(view);

		this.clearContent();

		if (!this.tryLoadLootTable(view)) ContainerHelper.loadAllItems(view, this.inventory);
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter components) {
		super.applyImplicitComponents(components);
		SeededContainerLoot containerLootComponent = components.get(DataComponents.CONTAINER_LOOT);
		if (containerLootComponent != null) {
			this.lootTable = containerLootComponent.lootTable();
			this.lootTableSeed = containerLootComponent.seed();
		}
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder componentMapBuilder) {
		super.collectImplicitComponents(componentMapBuilder);
		if (this.lootTable != null)
			componentMapBuilder.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.lootTable, this.lootTableSeed));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeComponentsFromTag(ValueOutput view) {
		super.removeComponentsFromTag(view);
		view.discard("LootTable");
		view.discard("LootTableSeed");
	}
}
