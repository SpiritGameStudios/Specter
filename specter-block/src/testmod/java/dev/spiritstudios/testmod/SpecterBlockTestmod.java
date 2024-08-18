package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.item.SpecterItemGroup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SpecterBlockTestmod implements ModInitializer {
	public static final SpecterItemGroup TEST_GROUP = new SpecterItemGroup(Identifier.of("specter_block_testmod", "test_group"), () -> new ItemStack(Blocks.ACACIA_LOG));

	public static final Block TEST_BLOCK = new Block(AbstractBlock.Settings.create().group(TEST_GROUP));

	@Override
	public void onInitialize() {
		// Group test
		Registry.register(Registries.BLOCK, Identifier.of("specter_block_testmod", "test_block"), TEST_BLOCK);
		BlockItem item = new BlockItem(TEST_BLOCK, new Item.Settings());
		Registry.register(Registries.ITEM, Identifier.of("specter_block_testmod", "test_block"), item);

		TEST_GROUP.init();

		// Stripped state test
		StrippableBlockRegistry.register(Blocks.SANDSTONE_STAIRS, Blocks.SMOOTH_QUARTZ_STAIRS);
	}
}
