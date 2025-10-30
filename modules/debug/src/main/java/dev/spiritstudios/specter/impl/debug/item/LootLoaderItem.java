package dev.spiritstudios.specter.impl.debug.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootLoaderItem extends Item {
	private static final Component NO_LOOT_TABLE = Component.translatable("item.specter-debug.loot_loader.no_loot_table");

	public LootLoaderItem(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		Level world = context.getLevel();
		if (player == null || world.isClientSide()) return InteractionResult.SUCCESS;

		BlockPos pos = context.getClickedPos();
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity == null) return InteractionResult.PASS;
		if (!(blockEntity instanceof RandomizableContainer lootableInventory)) return InteractionResult.PASS;

		ItemStack stack = context.getItemInHand();
		if (!stack.has(DataComponents.CUSTOM_NAME)) {
			player.displayClientMessage(NO_LOOT_TABLE, true);
			return InteractionResult.FAIL;
		}

		ResourceLocation lootTableId = ResourceLocation.tryParse(stack.getHoverName().getString());
		if (lootTableId == null) {
			player.displayClientMessage(
					Component.translatable(
							"item.specter-debug.loot_loader.invalid_id",
							stack.getHoverName().getString()
					),
					true
			);
			return InteractionResult.FAIL;
		}

		MinecraftServer server = world.getServer();
		if (server == null) return InteractionResult.FAIL;

		ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);
		if (server.reloadableRegistries().getLootTable(lootTable) == LootTable.EMPTY) {
			player.displayClientMessage(
					Component.translatable(
							"item.specter-debug.loot_loader.invalid_loot_table",
							lootTableId.toString()
					),
					true
			);
			return InteractionResult.FAIL;
		}

		lootableInventory.setLootTable(lootTable, world.random.nextLong());

		player.displayClientMessage(
				Component.translatable(
						"item.specter-debug.loot_loader.loaded_loot_table",
						lootTableId.toString()
				),
				true
		);
		return InteractionResult.SUCCESS;
	}
}
