package dev.spiritstudios.specter.api.item;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static dev.spiritstudios.specter.impl.core.Specter.MODID;

public final class ItemAttachments {
	public static final Attachment<Item, Float> COMPOSTING_CHANCE = Attachment.builder(
		Registries.ITEM,
		Identifier.of(MODID, "composting_chance"),
		Codec.floatRange(0.0F, 1.0F),
		PacketCodecs.FLOAT.cast()
	).build();

	public static final Attachment<Item, Integer> FUEL = Attachment.builder(
		Registries.ITEM,
		Identifier.of(MODID, "fuel"),
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
