package dev.spiritstudios.specter.api.worldgen;

import com.mojang.datafixers.util.Either;

import dev.spiritstudios.specter.mixin.worldgen.LegacySinglePoolElementAccessor;
import dev.spiritstudios.specter.mixin.worldgen.SinglePoolElementAccessor;
import dev.spiritstudios.specter.mixin.worldgen.StructurePoolElementAccessor;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public final class SpecterStructurePoolElements {
	public static Function<StructurePool.Projection, LegacySinglePoolElement> ofLegacySingle(Identifier id) {
		return projection -> LegacySinglePoolElementAccessor.createLegacySinglePoolElement(Either.left(id), StructurePoolElementAccessor.getEMPTY_PROCESSORS(), projection, Optional.empty());
	}

	public static Function<StructurePool.Projection, LegacySinglePoolElement> ofProcessedLegacySingle(
			Identifier id, RegistryEntry<StructureProcessorList> processorListEntry
	) {
		return projection -> LegacySinglePoolElementAccessor.createLegacySinglePoolElement(Either.left(id), processorListEntry, projection, Optional.empty());
	}

	public static Function<StructurePool.Projection, SinglePoolElement> ofSingle(Identifier id) {
		return projection -> SinglePoolElementAccessor.createSinglePoolElement(Either.left(id), StructurePoolElementAccessor.getEMPTY_PROCESSORS(), projection, Optional.empty());
	}

	public static Function<StructurePool.Projection, SinglePoolElement> ofProcessedSingle(Identifier id, RegistryEntry<StructureProcessorList> processorListEntry) {
		return projection -> SinglePoolElementAccessor.createSinglePoolElement(Either.left(id), processorListEntry, projection, Optional.empty());
	}

	public static Function<StructurePool.Projection, SinglePoolElement> ofSingle(Identifier id, StructureLiquidSettings liquidSettings) {
		return projection -> SinglePoolElementAccessor.createSinglePoolElement(Either.left(id), StructurePoolElementAccessor.getEMPTY_PROCESSORS(), projection, Optional.of(liquidSettings));
	}

	public static Function<StructurePool.Projection, SinglePoolElement> ofProcessedSingle(
			Identifier id, RegistryEntry<StructureProcessorList> processorListEntry, StructureLiquidSettings liquidSettings
	) {
		return projection -> SinglePoolElementAccessor.createSinglePoolElement(Either.left(id), processorListEntry, projection, Optional.of(liquidSettings));
	}
}
