package dev.spiritstudios.specter.api.item;

import com.mojang.serialization.Codec;

import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public final class ItemMetatags {
	public static final Metatag<Item, Float> COMPOSTING_CHANCE = Metatag.builder(
		Registries.ITEM,
		Identifier.of(SpecterGlobals.MODID, "composting_chance"),
		Codec.floatRange(0.0F, 1.0F),
		PacketCodecs.FLOAT.cast()
	).build();

	public static final Metatag<Item, Integer> FUEL = Metatag.builder(
		Registries.ITEM,
		Identifier.of(SpecterGlobals.MODID, "fuel"),
		Codec.intRange(0, 32767),
		PacketCodecs.INTEGER.cast()
	).build();

	/**
	 * Hacky workaround to force class loading.
	 */
	@SuppressWarnings("EmptyMethod")
	public static void init() {
		// NO-OP
	}
}
