package dev.spiritstudios.specter.impl.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

// FabricAPI is dumb and assumes all ItemGroups implement FabricItemGroupImpl
// Yes, I am aware that this is a bad practice, but I have no other choice
@SuppressWarnings("UnstableApiUsage")
public class DataItemGroup extends ItemGroup implements FabricItemGroupImpl {
	public static final Codec<DataItemGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("translate").forGetter(DataItemGroup::getTranslationKey),
		ItemStack.CODEC.fieldOf("icon").forGetter(DataItemGroup::getIcon),
		Codec.either(
			Registries.ITEM.getCodec(),
			ItemStack.CODEC
		).listOf().fieldOf("items").forGetter(DataItemGroup::getItems)
	).apply(instance, DataItemGroup::new));

	private final String translate;
	private final ItemStack icon;
	private final List<Either<Item, ItemStack>> items;
	private final List<ItemStack> displayStacks;

	public Row row;
	public int column;
	private int page;

	public DataItemGroup(String translationKey, ItemStack icon, List<Either<Item, ItemStack>> items) {
		super(
			null,
			-1,
			Type.CATEGORY,
			Text.translatable(translationKey),
			() -> icon,
			(displayContext, entries) -> items.forEach(entry -> {
				ItemStack stack = entry.map(ItemStack::new, itemStack -> itemStack);
				entries.add(stack);
			})
		);

		this.translate = translationKey;
		this.icon = icon;
		this.items = items;
		this.displayStacks = items.stream().map(entry -> entry.map(ItemStack::new, itemStack -> itemStack)).toList();
	}

	public void setup(List<ItemGroup> filtered, int offset) {
		int count = filtered.size() + offset;
		this.page = count / TABS_PER_PAGE;

		int pageIndex = count % TABS_PER_PAGE;
		ItemGroup.Row row = pageIndex < (TABS_PER_PAGE / 2) ? ItemGroup.Row.TOP : ItemGroup.Row.BOTTOM;
		this.row = row;
		this.column = row == ItemGroup.Row.TOP ? pageIndex % TABS_PER_PAGE : (pageIndex - TABS_PER_PAGE / 2) % (TABS_PER_PAGE);
	}

	public String getTranslationKey() {
		return translate;
	}

	@Override
	public ItemStack getIcon() {
		return icon;
	}

	public List<Either<Item, ItemStack>> getItems() {
		return items;
	}

	@Override
	public Collection<ItemStack> getDisplayStacks() {
		return displayStacks;
	}

	@Override
	public Collection<ItemStack> getSearchTabStacks() {
		return displayStacks;
	}

	@Override
	public boolean shouldDisplay() {
		return true;
	}

	@Override
	public Row getRow() {
		return row;
	}

	@Override
	public int getColumn() {
		return column;
	}

	@Override
	public int fabric_getPage() {
		return page;
	}

	@Override
	public void fabric_setPage(int page) {
	}
}
