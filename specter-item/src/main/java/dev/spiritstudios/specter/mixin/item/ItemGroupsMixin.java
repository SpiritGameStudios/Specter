package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.spiritstudios.specter.api.item.DataItemGroup;
import dev.spiritstudios.specter.impl.item.ItemGroupReloader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(ItemGroups.class)
public abstract class ItemGroupsMixin {
	@Shadow
	private static Stream<ItemGroup> stream() {
		return Stream.empty();
	}

	@ModifyReturnValue(method = {"getGroupsToDisplay", "getGroups"}, at = @At("RETURN"))
	private static List<ItemGroup> getGroups(List<ItemGroup> original) {
		List<ItemGroup> groups = new ArrayList<>(original);

		List<ItemGroup> filtered = stream()
			.filter(itemGroup -> itemGroup.getType() == ItemGroup.Type.CATEGORY && !itemGroup.isSpecial())
			.filter(ItemGroup::shouldDisplay)
			.toList();

		int offset = 0;
		for (DataItemGroup group : ItemGroupReloader.ITEM_GROUPS) {
			if (groups.contains(group)) continue;
			group.setup(filtered, offset);
			groups.add(group);
			offset++;
		}

		return groups;
	}
}
