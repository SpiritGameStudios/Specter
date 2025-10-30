package dev.spiritstudios.testmod.registry;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("unused")
public class SpecterRegistryGameTest {
	@GameTest
	public void testMetatagGet(GameTestHelper context) {
		context.assertValueEqual(
				SpecterRegistryTestMod.TEST_METATAG.get(Blocks.DIORITE).orElse(null),
				6969,
				Component.nullToEmpty("Metatag value")
		);

		context.succeed();
	}
}
