package dev.spiritstudios.specter.mixin.item;

import dev.spiritstudios.specter.api.item.SpecterItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(Item.Settings.class)
public class ItemSettingsMixin implements SpecterItem.Settings {
}
