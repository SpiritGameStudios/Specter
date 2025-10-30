package dev.spiritstudios.specter.api.item;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.core.Specter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;

public final class ItemMetatags {
	public static final Metatag<Item, Float> COMPOSTING_CHANCE = Metatag.builder(
			Registries.ITEM,
			Specter.id("composting_chance"),
			Codec.floatRange(0.0F, 1.0F)
	).packetCodec(ByteBufCodecs.FLOAT.cast()).build();

	public static final Metatag<Item, Integer> FUEL = Metatag.builder(
			Registries.ITEM,
			Specter.id("fuel"),
			Codec.intRange(0, 32767)
	).packetCodec(ByteBufCodecs.INT.cast()).build();

	/**
	 * Hacky workaround to force class loading.
	 */
	@SuppressWarnings("EmptyMethod")
	public static void init() {
		// NO-OP
	}
}
