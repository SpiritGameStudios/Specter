package dev.spiritstudios.testmod.registry;

import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

import dev.spiritstudios.specter.api.core.util.SpecterAssertions;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;

@SuppressWarnings("unused")
public class SpecterRegistryGameTest {
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
			"Chocolate \"specter-registry-testmod:john\""
		);

		context.complete();
	}
}
