package dev.spiritstudios.testmod.registry;

import net.minecraft.block.Blocks;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

@SuppressWarnings("unused")
public class SpecterRegistryGameTest {
	@GameTest
	public void testMetatagGet(TestContext context) {
		context.assertEquals(
				SpecterRegistryTestMod.TEST_METATAG.get(Blocks.DIORITE).orElse(null),
				6969,
				Text.of("Metatag value")
		);

		context.complete();
	}
}
