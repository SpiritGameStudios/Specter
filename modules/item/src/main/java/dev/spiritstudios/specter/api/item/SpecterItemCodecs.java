package dev.spiritstudios.specter.api.item;

import com.mojang.serialization.Codec;

import net.minecraft.item.ItemStack;

public final class SpecterItemCodecs {
	public static final Codec<ItemStack> ITEM_STACK_OR_NAME = Codec.withAlternative(
			ItemStack.CODEC,
			ItemStack.REGISTRY_ENTRY_CODEC
	);

	public static final Codec<ItemStack> UNCOUNTED_ITEM_STACK_OR_NAME = Codec.withAlternative(
			ItemStack.UNCOUNTED_CODEC,
			ItemStack.REGISTRY_ENTRY_CODEC
	);
}
