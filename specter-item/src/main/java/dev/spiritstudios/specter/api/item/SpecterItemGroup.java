package dev.spiritstudios.specter.api.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Simple wrapper around {@link ItemGroup} that allows for items to be added via {@link Item.Settings}.
 */
public class SpecterItemGroup {
	private final List<Item> items = new ArrayList<>();

	private final Identifier id;
	private final ItemGroup group;

	private boolean initialized = false;

	public SpecterItemGroup(Identifier id, Supplier<ItemStack> icon) {
		this.id = id;
		this.group = FabricItemGroup.builder()
			.icon(icon)
			.displayName(Text.translatable("itemGroup.%s.%s".formatted(id.getNamespace(), id.getPath())))
			.entries((displayContext, entries) -> addEntries(entries))
			.build();
	}

	public SpecterItemGroup(Identifier id, ItemConvertible icon) {
		this(id, () -> new ItemStack(icon.asItem()));
	}

	/**
	 * Initializes the item group. This must be called after all items have been added, as it will freeze the item list.
	 */
	public void init() {
		if (initialized) throw new IllegalStateException("Cannot initialize item group more than once");
		Registry.register(Registries.ITEM_GROUP, id, group);
		initialized = true;
	}

	private void addEntries(ItemGroup.Entries entries) {
		for (Item item : items) entries.add(new ItemStack(item));
	}

	public void addItem(Item item) {
		if (initialized) throw new IllegalStateException("Cannot add items after initialization");
		items.add(item);
	}
}
