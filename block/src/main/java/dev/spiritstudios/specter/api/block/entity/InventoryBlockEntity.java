package dev.spiritstudios.specter.api.block.entity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

/**
 * A simple {@link BlockEntity} with an inventory that is synchronized with the client.
 */
public class InventoryBlockEntity extends BlockEntity implements ImplementedInventory {
	protected final DefaultedList<ItemStack> inventory;

	public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
		super(type, pos, state);
		inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);

		Inventories.writeData(view, this.inventory);
	}


	@Override
	protected void readData(ReadView view) {
		super.readData(view);

		this.clear();
		Inventories.readData(view, this.inventory);
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	protected void readComponents(ComponentsAccess componentsAccess) {
		super.readComponents(componentsAccess);
		componentsAccess.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.getItems());
	}

	@Override
	protected void addComponents(ComponentMap.Builder builder) {
		super.addComponents(builder);
		builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.getItems()));
	}
}
