package dev.spiritstudios.testmod;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;

@SuppressWarnings("unused")
public class SpecterItemGameTest {
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testCompostingChanceAttachment(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.COMPOSTER);

		PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
		player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.QUARTZ_PILLAR));

		context.useBlock(pos, player);
		context.expectBlockProperty(pos, ComposterBlock.LEVEL, 1);
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 110)
	public void testFuelAttachment(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.BLAST_FURNACE);
		if (!(context.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity furnace))
			throw new AssertionError("Furnace was not placed");

		furnace.setStack(0, new ItemStack(Items.RAW_IRON, 1));
		context.setBlockState(pos.east(), Blocks.HOPPER.getDefaultState().with(HopperBlock.FACING, Direction.WEST));
		if (!(context.getBlockEntity(pos.east()) instanceof HopperBlockEntity hopper))
			throw new AssertionError("Hopper was not placed");

		hopper.setStack(0, new ItemStack(Items.QUARTZ_PILLAR, 1));
		context.waitAndRun(105, () -> {
			context.assertTrue(hopper.isEmpty(), "Hopper should be empty");
			context.assertTrue(ItemStack.areEqual(furnace.getStack(2), new ItemStack(Items.IRON_INGOT, 1)), "Furnace should have smelted 1 iron ingot");
			context.complete();
		});
	}
}
