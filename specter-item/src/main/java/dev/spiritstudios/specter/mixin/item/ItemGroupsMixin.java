package dev.spiritstudios.specter.mixin.item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

import dev.spiritstudios.specter.api.item.SpecterItemRegistryKeys;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import dev.spiritstudios.specter.impl.item.DataItemGroup;

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

		SpecterReloadableRegistries.lookup().ifPresent(manager -> {
			AtomicInteger offset = new AtomicInteger();
			manager.getOrThrow(SpecterItemRegistryKeys.ITEM_GROUP).streamEntries().forEach(r -> {
				DataItemGroup group = r.value();

				if (groups.contains(group)) return;

				group.setup(filtered, offset.get());
				groups.add(group);
				offset.getAndIncrement();
			});
		});

		return groups;
	}
}
