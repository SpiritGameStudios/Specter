package dev.spiritstudios.testmod.block;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class SpecterBlockTestMod implements ModInitializer {
	public static final Block TEST_BLOCK = new Block(BlockBehaviour.Properties.of()
			.setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("specter_block_testmod", "test_block"))));

	public static final Property<Boolean> TEST_PROPERTY = BooleanProperty.create("test_property");

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("specter_block_testmod", "test_block"), TEST_BLOCK);
		BlockItem item = new BlockItem(TEST_BLOCK, new Item.Properties()
				.setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("specter_block_testmod", "test_block"))));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("specter_block_testmod", "test_block"), item);

//		BlockStatePropertyModifications.add(Blocks.DIORITE, context -> {
//			context.add(TEST_PROPERTY, false);
//		});
	}
}
