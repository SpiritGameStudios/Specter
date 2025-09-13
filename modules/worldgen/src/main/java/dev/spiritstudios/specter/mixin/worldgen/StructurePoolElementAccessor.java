package dev.spiritstudios.specter.mixin.worldgen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;

@Mixin(StructurePoolElement.class)
public interface StructurePoolElementAccessor {
	@Accessor
	static RegistryEntry<StructureProcessorList> getEMPTY_PROCESSORS() {
		throw new UnsupportedOperationException();
	}
}
