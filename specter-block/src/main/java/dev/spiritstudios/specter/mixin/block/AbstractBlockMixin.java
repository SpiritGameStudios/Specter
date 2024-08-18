package dev.spiritstudios.specter.mixin.block;

import dev.spiritstudios.specter.api.block.SpecterBlock;
import dev.spiritstudios.specter.api.item.SpecterItemGroup;
import dev.spiritstudios.specter.impl.block.SpecterBlockImpl;
import net.minecraft.block.AbstractBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin implements SpecterBlock {

	@Unique
	@Nullable
	private SpecterItemGroup group;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(AbstractBlock.Settings settings, CallbackInfo ci) {
		SpecterBlockImpl.Data data = SpecterBlockImpl.computeIfAbsent(settings);
		if (data == null) return;

		group = data.getGroup();
	}

	@Override
	public @Nullable SpecterItemGroup specter$getGroup() {
		return group;
	}
}
