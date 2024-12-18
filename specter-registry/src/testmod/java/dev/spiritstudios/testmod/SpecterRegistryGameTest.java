package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.core.util.SpecterAssertions;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class SpecterRegistryGameTest {
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testRegistrar(TestContext context) {
		context.setBlockState(new BlockPos(0, 1, 0), SpecterRegistryTestBlocks.TEST_BLOCK);

		context.expectBlock(SpecterRegistryTestBlocks.TEST_BLOCK, new BlockPos(0, 1, 0));
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testMetatagGet(TestContext context) {
		context.assertEquals(
			SpecterRegistryTestMod.TEST_METATAG.get(Blocks.DIORITE).orElse(null),
			6969,
			"Metatag value"
		);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testMetatagPut(TestContext context) {
		SpecterRegistryTestMod.TEST_METATAG.put(Blocks.GRANITE, 420);
		context.assertEquals(
			SpecterRegistryTestMod.TEST_METATAG.get(Blocks.GRANITE).orElse(null),
			420,
			"Metatag value"
		);

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testClientMetatagIsolation(TestContext context) {
		SpecterAssertions.assertThrows(
			AssertionError.class,
			() -> SpecterRegistryTestMod.TEST_CLIENT_METATAG.get(Blocks.DIORITE),
			"Client Metatag was not properly isolated from server"
		);
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testReloadableRegistry(TestContext context) {
		Chocolate john = SpecterReloadableRegistries.reloadableManager().orElseThrow()
			.get(SpecterRegistryTestMod.CHOCOLATE_KEY)
			.get(Identifier.of(SpecterRegistryTestMod.MODID, "john"));

		context.assertEquals(
			john,
			new Chocolate(
				Chocolate.Type.DARK,
				Chocolate.NutType.DEEZ
			),
			"Chocolate \"specter-registry-testmod:john\""
		);

		context.complete();
	}
}
