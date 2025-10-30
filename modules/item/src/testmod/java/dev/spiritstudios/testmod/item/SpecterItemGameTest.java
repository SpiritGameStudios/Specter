package dev.spiritstudios.testmod.item;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

@SuppressWarnings("unused")
public class SpecterItemGameTest {
	@GameTest
	public void testCompostingChanceMetatag(GameTestHelper context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlock(pos, Blocks.COMPOSTER);

		Player player = context.makeMockPlayer(GameType.SURVIVAL);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.QUARTZ_PILLAR));

		context.useBlock(pos, player);
		context.assertBlockProperty(pos, ComposterBlock.LEVEL, 1);
		context.succeed();
	}

	@GameTest(maxTicks = 110)
	public void testFuelMetatag(GameTestHelper context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlock(pos, Blocks.BLAST_FURNACE);
		if (!(context.getBlockEntity(pos, AbstractFurnaceBlockEntity.class) instanceof AbstractFurnaceBlockEntity furnace))
			throw new AssertionError("Furnace was not placed");

		furnace.setItem(0, new ItemStack(Items.RAW_IRON, 1));
		context.setBlock(pos.east(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.WEST));
		if (!(context.getBlockEntity(pos.east(), HopperBlockEntity.class) instanceof HopperBlockEntity hopper))
			throw new AssertionError("Hopper was not placed");

		hopper.setItem(0, new ItemStack(Items.QUARTZ_PILLAR, 1));
		context.runAfterDelay(105, () -> {
			context.assertTrue(hopper.isEmpty(), Component.nullToEmpty("Hopper should be empty"));
			context.assertTrue(ItemStack.matches(furnace.getItem(2), new ItemStack(Items.IRON_INGOT, 1)), Component.nullToEmpty("Furnace should have smelted 1 iron ingot"));
			context.succeed();
		});
	}
}
