package dev.spiritstudios.testmod;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

@SuppressWarnings("unused")
public final class SpecterBlockGameTest {
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testStrippableMetatag(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.QUARTZ_PILLAR);

		PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
		player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));

		context.useBlock(pos, player);
		context.expectBlock(Blocks.PURPUR_PILLAR, pos);
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testFlattenableMetatag(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.QUARTZ_PILLAR);

		PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
		player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_SHOVEL));

		context.useBlock(pos, player);
		context.expectBlock(Blocks.QUARTZ_SLAB, pos);
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testWaxableMetatag(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.QUARTZ_PILLAR);

		PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
		player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.HONEYCOMB));

		context.useBlock(pos, player);
		context.expectBlock(Blocks.QUARTZ_BRICKS, pos);
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testOxidizableMetatag(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.DIAMOND_ORE);

		PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
		player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));

		context.useBlock(pos, player);
		context.expectBlock(Blocks.IRON_ORE, pos);

		context.useBlock(pos, player);
		context.expectBlock(Blocks.GOLD_ORE, pos);

		context.useBlock(pos, player);
		context.expectBlock(Blocks.COPPER_ORE, pos);

		context.useBlock(pos, player);
		context.expectBlock(Blocks.COPPER_ORE, pos);

		context.complete();
	}
}
