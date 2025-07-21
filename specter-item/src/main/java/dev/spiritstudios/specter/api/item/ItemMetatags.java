package dev.spiritstudios.specter.api.item;

import com.mojang.serialization.Codec;

import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.core.Specter;

public final class ItemMetatags {
	public static final Metatag<Item, Float> COMPOSTING_CHANCE = Metatag.builder(
			RegistryKeys.ITEM,
			Specter.id("composting_chance"),
			Codec.floatRange(0.0F, 1.0F)
	).packetCodec(PacketCodecs.FLOAT.cast()).build();

	public static final Metatag<Item, Integer> FUEL = Metatag.builder(
			RegistryKeys.ITEM,
			Specter.id("fuel"),
			Codec.intRange(0, 32767)
	).packetCodec(PacketCodecs.INTEGER.cast()).build();

	/**
	 * Hacky workaround to force class loading.
	 */
	@SuppressWarnings("EmptyMethod")
	public static void init() {
		// NO-OP
	}
}
