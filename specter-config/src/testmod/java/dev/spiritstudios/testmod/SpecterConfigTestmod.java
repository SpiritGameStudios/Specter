package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SpecterConfigTestmod implements ModInitializer {
	public static final Block TEST_BLOCK = new ConfigTestBlock(AbstractBlock.Settings.create());
	public final static TestConfig CONFIG = ConfigManager.getConfig(TestConfig.class);

	@Override
	public void onInitialize() {
		Registry.register(Registries.BLOCK, Identifier.of("specter_config_testmod", "test_block"), TEST_BLOCK);
		BlockItem item = new BlockItem(TEST_BLOCK, new Item.Settings());
		Registry.register(Registries.ITEM, Identifier.of("specter_config_testmod", "test_block"), item);
	}
}
