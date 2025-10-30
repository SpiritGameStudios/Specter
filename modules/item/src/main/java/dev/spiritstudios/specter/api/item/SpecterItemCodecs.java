package dev.spiritstudios.specter.api.item;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

public final class SpecterItemCodecs {
	public static final Codec<ItemStack> ITEM_STACK_OR_NAME = Codec.withAlternative(
			ItemStack.CODEC,
			ItemStack.SIMPLE_ITEM_CODEC
	);

	public static final Codec<ItemStack> UNCOUNTED_ITEM_STACK_OR_NAME = Codec.withAlternative(
			ItemStack.SINGLE_ITEM_CODEC,
			ItemStack.SIMPLE_ITEM_CODEC
	);
}
