package dev.spiritstudios.specter.api.item;

import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DataItemGroup extends CreativeModeTab {
	public static final Codec<DataItemGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ComponentSerialization.CODEC.fieldOf("display_name").forGetter(CreativeModeTab::getDisplayName),
			ItemStack.SINGLE_ITEM_CODEC.fieldOf("icon").forGetter(CreativeModeTab::getIconItem),
			SpecterItemCodecs.UNCOUNTED_ITEM_STACK_OR_NAME.listOf().fieldOf("items").forGetter(group -> group.items)
	).apply(instance, DataItemGroup::new));

	private final List<ItemStack> items;

	public DataItemGroup(Component displayName, ItemStack icon, List<ItemStack> items) {
		super(
				null,
				-1,
				Type.CATEGORY,
				displayName,
				() -> icon,
				(displayContext, entries) -> {
					for (ItemStack item : items) {
						entries.accept(item);
					}
				}
		);

		this.items = items;
	}

	public DataItemGroup(Component displayName, ItemStack icon, ItemStack... items) {
		super(
				null,
				-1,
				Type.CATEGORY,
				displayName,
				() -> icon,
				(displayContext, entries) -> {
					for (ItemStack item : items) {
						entries.accept(item);
					}
				}
		);

		this.items = Arrays.asList(items);
	}

	public DataItemGroup(Component displayName, ItemLike icon, List<ItemStack> items) {
		this(displayName, new ItemStack(icon), items);
	}

	public DataItemGroup(Component displayName, ItemLike icon, ItemStack... items) {
		this(displayName, new ItemStack(icon), items);
	}
}
