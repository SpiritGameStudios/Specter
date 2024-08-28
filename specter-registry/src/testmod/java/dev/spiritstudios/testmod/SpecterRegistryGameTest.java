package dev.spiritstudios.testmod;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class SpecterRegistryGameTest {
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testRegistrar(TestContext context) {
		context.setBlockState(new BlockPos(0, 1, 0), SpecterRegistryTestBlockRegistrar.TEST_BLOCK);

		context.expectBlock(SpecterRegistryTestBlockRegistrar.TEST_BLOCK, new BlockPos(0, 1, 0));
		context.complete();
	}
}
