package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.block.BlockStatePropertyModifications;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

public class SpecterBlockTestmod implements ModInitializer {
	public static final Block TEST_BLOCK = new Block(AbstractBlock.Settings.create());

	public static final Property<Boolean> TEST_PROPERTY = BooleanProperty.of("test_property");

	@Override
	public void onInitialize() {
		Registry.register(Registries.BLOCK, Identifier.of("specter_block_testmod", "test_block"), TEST_BLOCK);
		BlockItem item = new BlockItem(TEST_BLOCK, new Item.Settings());
		Registry.register(Registries.ITEM, Identifier.of("specter_block_testmod", "test_block"), item);

		BlockStatePropertyModifications.add(TEST_BLOCK, TEST_PROPERTY, false);
	}
}
