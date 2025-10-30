package dev.spiritstudios.testmod.dfu;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import dev.spiritstudios.specter.api.dfu.DataFixHelper;
import dev.spiritstudios.specter.api.dfu.SpecterDataFixRegistry;

public class SpecterDfuTestMod implements ModInitializer {
	private static final Item TEST_ITEM = Registry.register(
		BuiltInRegistries.ITEM,
		ResourceLocation.fromNamespaceAndPath("specter_dfu_testmod", "test_item_new"),
		new Item(new Item.Properties()
			.setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("specter_dfu_testmod", "test_item_new"))))
	);

	@Override
	public void onInitialize() {
		DataFixerBuilder build = new DataFixerBuilder(4);
		build.addSchema(0, (version, parent) -> SpecterDataFixRegistry.createRootSchema());

		Schema renameItemSchema = build.addSchema(4, Schema::new);
		DataFixHelper.renameItem(
			build,
			"Rename test_item to test_item_new",
			ResourceLocation.fromNamespaceAndPath("specter_dfu_testmod", "test_item_new"),
			ResourceLocation.fromNamespaceAndPath("specter_dfu_testmod", "test_item"),
			renameItemSchema
		);

		SpecterDataFixRegistry.register(
			"specter_dfu_testmod",
			4,
			build.build().fixer()
		);
	}
}
