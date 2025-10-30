package dev.spiritstudios.specter.mixin.block;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
	@Accessor
	static Map<Block, Block> getSTRIPPABLES() {
		throw new UnreachableException();
	}
}
