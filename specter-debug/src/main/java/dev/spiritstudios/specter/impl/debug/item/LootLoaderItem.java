package dev.spiritstudios.specter.impl.debug.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LootLoaderItem extends Item {
	private static final Text NO_LOOT_TABLE = Text.translatable("item.specter-debug.loot_loader.no_loot_table");
	private static final Text INVALID_LOOT_TABLE = Text.translatable("item.specter-debug.loot_loader.invalid_loot_table");
	private static final Text LOADED_LOOT_TABLE = Text.translatable("item.specter-debug.loot_loader.loaded_loot_table");

	public LootLoaderItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity player = context.getPlayer();
		World world = context.getWorld();
		if (player == null || world.isClient()) return ActionResult.PASS;

		BlockPos pos = context.getBlockPos();
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity == null) return ActionResult.PASS;
		if (!(blockEntity instanceof LootableInventory lootableInventory)) return ActionResult.PASS;

		ItemStack stack = context.getStack();
		if (!stack.contains(DataComponentTypes.CUSTOM_NAME)) {
			player.sendMessage(NO_LOOT_TABLE, true);
			return ActionResult.FAIL;
		}

		String[] split = stack.getName().getString().split(":");
		Identifier lootTableId = Identifier.of(split[0], split[1]);

		MinecraftServer server = world.getServer();
		if (server == null) return ActionResult.FAIL;

		RegistryKey<LootTable> lootTable = RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId);
		if (server.getReloadableRegistries().getLootTable(lootTable) == LootTable.EMPTY) {
			player.sendMessage(INVALID_LOOT_TABLE, true);
			return ActionResult.FAIL;
		}

		lootableInventory.setLootTable(lootTable, world.random.nextLong());

		player.sendMessage(LOADED_LOOT_TABLE, true);
		return ActionResult.SUCCESS;
	}
}
