package dev.spiritstudios.specter.mixin.block;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;
import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
	@Accessor("STRIPPED_BLOCKS")
	static Map<Block, Block> getStrippedBlocks() {
		throw new UnreachableException();
	}
}
