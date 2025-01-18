package dev.spiritstudios.specter.testmod;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import dev.spiritstudios.specter.api.dfu.DataFixHelper;
import dev.spiritstudios.specter.api.dfu.SpecterDataFixRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class SpecterDfuTestmod implements ModInitializer {
	private static final Item TEST_ITEM = Registry.register(
		Registries.ITEM,
		Identifier.of("specter_dfu_testmod", "test_item"),
		new Item(new Item.Settings()
			.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("specter_dfu_testmod", "test_item"))))
	);

	@Override
	public void onInitialize() {
		DataFixerBuilder build = new DataFixerBuilder(4);
		build.addSchema(0, (version, parent) -> SpecterDataFixRegistry.createRootSchema());

		Schema renameItemSchema = build.addSchema(4, IdentifierNormalizingSchema::new);
		DataFixHelper.renameItem(
			build,
			"Rename test_item to test_item_new",
			Identifier.of("specter_dfu_testmod", "test_item_new"),
			Identifier.of("specter_dfu_testmod", "test_item"),
			renameItemSchema
		);

		SpecterDataFixRegistry.register(
			"specter_dfu_testmod",
			4,
			build.build().fixer()
		);
	}
}
