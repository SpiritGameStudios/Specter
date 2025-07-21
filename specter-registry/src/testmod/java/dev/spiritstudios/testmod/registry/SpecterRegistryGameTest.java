package dev.spiritstudios.testmod.registry;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;

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

	@GameTest
	public void testReloadableRegistry(TestContext context) {
		Chocolate john = SpecterReloadableRegistries.lookup().orElseThrow()
				.getOrThrow(SpecterRegistryTestMod.CHOCOLATE_KEY)
				.getOrThrow(RegistryKey.of(
						SpecterRegistryTestMod.CHOCOLATE_KEY,
						Identifier.of(SpecterRegistryTestMod.MODID, "john")
				)).value();

		context.assertEquals(
				john,
				new Chocolate(
						Chocolate.Type.DARK,
						Chocolate.NutType.DEEZ,
						"none"
				),
				Text.of("Chocolate \"specter-registry-testmod:john\"")
		);

		context.complete();
	}
}
