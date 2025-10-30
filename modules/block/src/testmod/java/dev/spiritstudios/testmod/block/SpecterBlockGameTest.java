package dev.spiritstudios.testmod.block;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("unused")
public final class SpecterBlockGameTest {
	@GameTest
	public void testStrippableMetatag(GameTestHelper context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlock(pos, Blocks.QUARTZ_PILLAR);

		Player player = context.makeMockPlayer(GameType.SURVIVAL);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));

		context.useBlock(pos, player);
		context.assertBlockPresent(Blocks.PURPUR_PILLAR, pos);
		context.succeed();
	}

	@GameTest
	public void testFlattenableMetatag(GameTestHelper context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlock(pos, Blocks.QUARTZ_PILLAR);

		Player player = context.makeMockPlayer(GameType.SURVIVAL);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_SHOVEL));

		context.useBlock(pos, player);
		context.assertBlockPresent(Blocks.QUARTZ_SLAB, pos);
		context.succeed();
	}

	@GameTest
	public void testWaxableMetatag(GameTestHelper context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlock(pos, Blocks.QUARTZ_PILLAR);

		Player player = context.makeMockPlayer(GameType.SURVIVAL);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.HONEYCOMB));

		context.useBlock(pos, player);
		context.assertBlockPresent(Blocks.QUARTZ_BRICKS, pos);
		context.succeed();
	}

	@GameTest
	public void testOxidizableMetatag(GameTestHelper context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlock(pos, Blocks.DIAMOND_ORE);

		Player player = context.makeMockPlayer(GameType.SURVIVAL);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));

		context.useBlock(pos, player);
		context.assertBlockPresent(Blocks.IRON_ORE, pos);

		context.useBlock(pos, player);
		context.assertBlockPresent(Blocks.GOLD_ORE, pos);

		context.useBlock(pos, player);
		context.assertBlockPresent(Blocks.COPPER_ORE, pos);

		context.useBlock(pos, player);
		context.assertBlockPresent(Blocks.COPPER_ORE, pos);

		context.succeed();
	}

//	@GameTest
//	public void testBlockStatePropertyModification(TestContext context) {
//		BlockPos pos = new BlockPos(0, 1, 0);
//
//		context.setBlockState(pos, SpecterBlockTestMod.TEST_BLOCK.getDefaultState());
//		context.expectBlockProperty(pos, SpecterBlockTestMod.TEST_PROPERTY, false);
//
//		context.setBlockState(pos, SpecterBlockTestMod.TEST_BLOCK.getDefaultState().with(SpecterBlockTestMod.TEST_PROPERTY, true));
//		context.expectBlockProperty(pos, SpecterBlockTestMod.TEST_PROPERTY, true);
//
//		context.complete();
//	}
}
