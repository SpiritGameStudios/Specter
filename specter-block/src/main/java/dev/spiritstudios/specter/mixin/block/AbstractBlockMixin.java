package dev.spiritstudios.specter.mixin.block;

import dev.spiritstudios.specter.api.block.SpecterBlock;
import dev.spiritstudios.specter.api.item.SpecterItemGroup;
import dev.spiritstudios.specter.impl.block.SpecterBlockImpl;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
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
	private Block strippedBlock;

	@Unique
	@Nullable
	private SpecterItemGroup group;

	@Unique
	private int spread = 5;

	@Unique
	private int burn = 5;

	@Unique
	private boolean flammable;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(AbstractBlock.Settings settings, CallbackInfo ci) {
		SpecterBlockImpl.Data data = SpecterBlockImpl.computeIfAbsent(settings);
		if (data == null) return;

		strippedBlock = data.getStrippedBlock();
		group = data.getGroup();
		spread = data.getSpread();
		burn = data.getBurn();
		flammable = data.isFlammable();
	}

	@Override
	public @Nullable Block specter$getStrippedBlock() {
		return strippedBlock;
	}

	@Override
	public @Nullable SpecterItemGroup specter$getGroup() {
		return group;
	}

	@Override
	public int specter$getSpread() {
		return spread;
	}

	@Override
	public int specter$getBurn() {
		return burn;
	}

	@Override
	public boolean specter$isFlammable() {
		return flammable;
	}
}
