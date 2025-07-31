package dev.spiritstudios.specter.api.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class DataItemGroup extends ItemGroup {
	public static final Codec<DataItemGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TextCodecs.CODEC.fieldOf("display_name").forGetter(ItemGroup::getDisplayName),
			ItemStack.UNCOUNTED_CODEC.fieldOf("icon").forGetter(ItemGroup::getIcon),
			SpecterItemCodecs.UNCOUNTED_ITEM_STACK_OR_NAME.listOf().fieldOf("items").forGetter(group -> group.items)
	).apply(instance, DataItemGroup::new));

	private final List<ItemStack> items;

	public DataItemGroup(Text displayName, ItemStack icon, List<ItemStack> items) {
		super(
				null,
				-1,
				Type.CATEGORY,
				displayName,
				() -> icon,
				(displayContext, entries) -> {
					for (ItemStack item : items) {
						entries.add(item);
					}
				}
		);

		this.items = items;
	}

	public DataItemGroup(Text displayName, ItemStack icon, ItemStack... items) {
		super(
				null,
				-1,
				Type.CATEGORY,
				displayName,
				() -> icon,
				(displayContext, entries) -> {
					for (ItemStack item : items) {
						entries.add(item);
					}
				}
		);

		this.items = Arrays.asList(items);
	}

	public DataItemGroup(Text displayName, ItemConvertible icon, List<ItemStack> items) {
		this(displayName, new ItemStack(icon), items);
	}

	public DataItemGroup(Text displayName, ItemConvertible icon, ItemStack... items) {
		this(displayName, new ItemStack(icon), items);
	}
}
