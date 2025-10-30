package dev.spiritstudios.specter.mixin.worldgen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

@Mixin(StructurePoolElement.class)
public interface StructurePoolElementAccessor {
	@Accessor
	static Holder<StructureProcessorList> getEMPTY() {
		throw new UnsupportedOperationException();
	}
}
