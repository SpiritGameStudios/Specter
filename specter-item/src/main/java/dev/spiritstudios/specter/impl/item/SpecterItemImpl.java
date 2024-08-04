package dev.spiritstudios.specter.impl.item;

import dev.spiritstudios.specter.api.item.SpecterItemGroup;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

public final class SpecterItemImpl {
	private static final WeakHashMap<Item.Settings, Data> DATA = new WeakHashMap<>();

	public static Data computeIfAbsent(Item.Settings settings) {
		return DATA.computeIfAbsent(settings, s -> new Data());
	}

	public static Data get(Item.Settings settings) {
		return DATA.get(settings);
	}

	public static final class Data {
		@Nullable
		private SpecterItemGroup group;

		public void group(SpecterItemGroup group) {
			this.group = group;
		}

		public @Nullable SpecterItemGroup getGroup() {
			return group;
		}
	}
}
