package dev.spiritstudios.specter.impl.item;

import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import dev.spiritstudios.specter.api.item.SpecterItemCodecs;

// FabricAPI is dumb and assumes all ItemGroups implement FabricItemGroupImpl
// Yes, I am aware that this is bad practice, but I have no other choice
@SuppressWarnings("UnstableApiUsage")
public class DataItemGroup extends ItemGroup implements FabricItemGroupImpl {
	public static final Codec<DataItemGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TextCodecs.CODEC.fieldOf("display_name").forGetter(ItemGroup::getDisplayName),
			ItemStack.CODEC.fieldOf("icon").forGetter(ItemGroup::getIcon),
			SpecterItemCodecs.ITEM_STACK_OR_NAME.listOf().fieldOf("items").forGetter(group -> group.items)
	).apply(instance, DataItemGroup::new));
	
	private final ItemStack icon;
	private final List<ItemStack> items;

	public Row row;
	public int column;
	private int page;

	public DataItemGroup(Text displayName, ItemStack icon, List<ItemStack> items) {
		super(
				null,
				-1,
				Type.CATEGORY,
				displayName,
				() -> icon,
				(displayContext, entries) -> items.forEach(entries::add)
		);

		this.icon = icon;
		this.items = items;
	}

	public DataItemGroup(Text displayName, ItemConvertible icon, List<ItemStack> items) {
		this(displayName, new ItemStack(icon), items);
	}

	@ApiStatus.Internal
	public void setup(List<ItemGroup> filtered, int offset) {
		int count = filtered.size() + offset;
		this.page = count / TABS_PER_PAGE;

		int pageIndex = count % TABS_PER_PAGE;
		ItemGroup.Row row = pageIndex < (TABS_PER_PAGE / 2) ? ItemGroup.Row.TOP : ItemGroup.Row.BOTTOM;
		this.row = row;
		this.column = row == ItemGroup.Row.TOP ? pageIndex % TABS_PER_PAGE : (pageIndex - TABS_PER_PAGE / 2) % (TABS_PER_PAGE);
	}

	@Override
	public ItemStack getIcon() {
		return icon;
	}

	@Override
	public Collection<ItemStack> getDisplayStacks() {
		return items;
	}

	@Override
	public Collection<ItemStack> getSearchTabStacks() {
		return items;
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
