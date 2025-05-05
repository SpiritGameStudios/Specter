package dev.spiritstudios.specter.api.dfu;

import java.util.Objects;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixer.fix.BlockNameFix;
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
				) ? newId.toString() : oldId.toString()
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
				) ? newId.toString() : oldId.toString()
		));
	}
}
