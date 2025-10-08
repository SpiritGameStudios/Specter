package dev.spiritstudios.specter.api.dfu;

import java.util.Objects;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixer.fix.BlockNameFix;
import net.minecraft.datafixer.fix.EntityRenameFix;
import net.minecraft.datafixer.fix.ItemNameFix;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.Identifier;

public final class DataFixHelper {
	public static void renameBlock(
		DataFixerBuilder builder,
		String fixName,
		Identifier oldId, Identifier newId,
		Schema schema
	) {
		builder.addFixer(BlockNameFix.create(
			schema,
			fixName,
			input ->
				Objects.equals(
					IdentifierNormalizingSchema.normalize(input),
					oldId.toString()
				) ? newId.toString() : input
		));
	}

	public static void renameItem(
		DataFixerBuilder builder,
		String fixName,
		Identifier oldId, Identifier newId,
		Schema schema
	) {
		builder.addFixer(ItemNameFix.create(
			schema,
			fixName,
			input ->
				Objects.equals(
					IdentifierNormalizingSchema.normalize(input),
					oldId.toString()
				) ? newId.toString() : input
		));
	}

	public static void renameEntity(
			DataFixerBuilder builder,
			String fixName,
			Identifier oldId, Identifier newId,
			Schema schema
	) {
		builder.addFixer(new EntityRenameFix(fixName, schema, false) {
			@Override
			protected String rename(String oldName) {
				return Objects.equals(
						IdentifierNormalizingSchema.normalize(oldName),
						oldId.toString()
				) ? newId.toString() : oldName;
			}
		});
	}

	public static void renameBlockState(
			DataFixerBuilder builder,
			String fixName,
			Identifier blockId,
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
