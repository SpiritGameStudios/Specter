package dev.spiritstudios.specter.api.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * A simple {@link BlockEntity} with an inventory that is synchronized with the client.
 */
public class InventoryBlockEntity extends BlockEntity implements ImplementedInventory {
	protected final NonNullList<ItemStack> inventory;

	public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
		super(type, pos, state);
		inventory = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	protected void saveAdditional(ValueOutput view) {
		super.saveAdditional(view);

		ContainerHelper.saveAllItems(view, this.inventory);
	}


	@Override
	protected void loadAdditional(ValueInput view) {
		super.loadAdditional(view);

		this.clearContent();
		ContainerHelper.loadAllItems(view, this.inventory);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter componentsAccess) {
		super.applyImplicitComponents(componentsAccess);
		componentsAccess.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getItems());
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder builder) {
		super.collectImplicitComponents(builder);
		builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
	}
}
