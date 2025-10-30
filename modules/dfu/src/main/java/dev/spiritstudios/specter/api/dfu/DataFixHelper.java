package dev.spiritstudios.specter.api.dfu;

import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.ItemRenameFix;
import net.minecraft.util.datafix.fixes.SimplestEntityRenameFix;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;

public final class DataFixHelper {
	public static void renameBlock(
		DataFixerBuilder builder,
		String fixName,
		ResourceLocation oldId, ResourceLocation newId,
		Schema schema
	) {
		builder.addFixer(BlockRenameFix.create(
			schema,
			fixName,
			input ->
				Objects.equals(
					NamespacedSchema.ensureNamespaced(input),
					oldId.toString()
				) ? newId.toString() : input
		));
	}

	public static void renameItem(
		DataFixerBuilder builder,
		String fixName,
		ResourceLocation oldId, ResourceLocation newId,
		Schema schema
	) {
		builder.addFixer(ItemRenameFix.create(
			schema,
			fixName,
			input ->
				Objects.equals(
					NamespacedSchema.ensureNamespaced(input),
					oldId.toString()
				) ? newId.toString() : input
		));
	}

	public static void renameEntity(
			DataFixerBuilder builder,
			String fixName,
			ResourceLocation oldId, ResourceLocation newId,
			Schema schema
	) {
		builder.addFixer(new SimplestEntityRenameFix(fixName, schema, false) {
			@Override
			protected String rename(String oldName) {
				return Objects.equals(
						NamespacedSchema.ensureNamespaced(oldName),
						oldId.toString()
				) ? newId.toString() : oldName;
			}
		});
	}

	public static void renameBlockState(
			DataFixerBuilder builder,
			String fixName,
			ResourceLocation blockId,
			String oldState, String newState,
			String defaultValue,
			Schema schema
	) {
		builder.addFixer(new BlockStateRenameFix(
				fixName,
				blockId,
				oldState, newState,
				defaultValue,
				schema
		));
	}
}
