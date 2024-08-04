package dev.spiritstudios.specter.api.item;

import dev.spiritstudios.specter.impl.item.SpecterItemImpl;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * Extension of {@link Item} that allows for additional properties to be set.
 */
public interface SpecterItem {
	@Nullable
	SpecterItemGroup specter$getGroup();

	/**
	 * Extension of {@link Item.Settings} that allows for additional properties to be set.
	 * Do not use this interface directly, it is injected into {@link Item.Settings} for you.
	 */
	interface Settings {
		default Item.Settings group(SpecterItemGroup group) {
			SpecterItemImpl.computeIfAbsent((Item.Settings) this).group(group);
			return (Item.Settings) this;
		}
	}
}
