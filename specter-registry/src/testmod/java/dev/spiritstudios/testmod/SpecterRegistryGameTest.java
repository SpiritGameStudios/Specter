package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.core.util.SpecterAssertions;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
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

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testAttachmentGet(TestContext context) {
		context.assertEquals(
			SpecterRegistryTestMod.TEST_ATTACHMENT.get(Blocks.DIORITE).orElse(null),
			6969,
			"Attachment value"
		);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testAttachmentPut(TestContext context) {
		SpecterRegistryTestMod.TEST_ATTACHMENT.put(Blocks.GRANITE, 420);
		context.assertEquals(
			SpecterRegistryTestMod.TEST_ATTACHMENT.get(Blocks.GRANITE).orElse(null),
			420,
			"Attachment value"
		);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testClientAttachmentIsolation(TestContext context) {
		SpecterAssertions.assertThrows(
			AssertionError.class,
			() -> SpecterRegistryTestMod.TEST_CLIENT_ATTACHMENT.get(Blocks.DIORITE),
			"Client attachment was not properly isolated from server"
		);
		context.complete();
	}
}
