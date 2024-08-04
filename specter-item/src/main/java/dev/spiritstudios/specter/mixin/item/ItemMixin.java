package dev.spiritstudios.specter.mixin.item;

import dev.spiritstudios.specter.api.item.SpecterItem;
import dev.spiritstudios.specter.api.item.SpecterItemGroup;
import dev.spiritstudios.specter.impl.item.SpecterItemImpl;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin implements SpecterItem {
	@Unique
	@Nullable
	private SpecterItemGroup group;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(Item.Settings settings, CallbackInfo ci) {
		SpecterItemImpl.Data data = SpecterItemImpl.computeIfAbsent(settings);
		if (data == null) return;

		group = data.getGroup();
	}

	@Override
	public @Nullable SpecterItemGroup specter$getGroup() {
		return group;
	}
}
