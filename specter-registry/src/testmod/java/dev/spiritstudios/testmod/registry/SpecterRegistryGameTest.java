package dev.spiritstudios.testmod.registry;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

@SuppressWarnings("unused")
public class SpecterRegistryGameTest {
	@GameTest
	public void testMetatagGetByObject(TestContext context) {
		context.assertEquals(
				SpecterRegistryTestMod.TEST_METATAG.get(Blocks.DIORITE).orElse(null),
				6969,
				Text.of("Metatag value")
		);

		context.complete();
	}

	@GameTest
	public void testMetatagGetByEntry(TestContext context) {
		RegistryEntry<Block> diorite = Registries.BLOCK.getEntry(Blocks.DIORITE);

		context.assertEquals(
				SpecterRegistryTestMod.TEST_METATAG.get(diorite).orElse(null),
				6969,
				Text.of("Metatag value")
		);

		context.complete();
	}
}
