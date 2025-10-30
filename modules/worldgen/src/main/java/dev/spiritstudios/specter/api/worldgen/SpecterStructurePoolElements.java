package dev.spiritstudios.specter.api.worldgen;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import dev.spiritstudios.specter.mixin.worldgen.LegacySinglePoolElementAccessor;
import dev.spiritstudios.specter.mixin.worldgen.SinglePoolElementAccessor;
import dev.spiritstudios.specter.mixin.worldgen.StructurePoolElementAccessor;

public final class SpecterStructurePoolElements {
	public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> ofLegacySingle(ResourceLocation id) {
		return projection -> LegacySinglePoolElementAccessor.createLegacySinglePoolElement(Either.left(id), StructurePoolElementAccessor.getEMPTY(), projection, Optional.empty());
	}

	public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> ofProcessedLegacySingle(
			ResourceLocation id, Holder<StructureProcessorList> processorListEntry
	) {
		return projection -> LegacySinglePoolElementAccessor.createLegacySinglePoolElement(Either.left(id), processorListEntry, projection, Optional.empty());
	}

	public static Function<StructureTemplatePool.Projection, SinglePoolElement> ofSingle(ResourceLocation id) {
		return projection -> SinglePoolElementAccessor.createSinglePoolElement(Either.left(id), StructurePoolElementAccessor.getEMPTY(), projection, Optional.empty());
	}

	public static Function<StructureTemplatePool.Projection, SinglePoolElement> ofProcessedSingle(ResourceLocation id, Holder<StructureProcessorList> processorListEntry) {
		return projection -> SinglePoolElementAccessor.createSinglePoolElement(Either.left(id), processorListEntry, projection, Optional.empty());
	}

	public static Function<StructureTemplatePool.Projection, SinglePoolElement> ofSingle(ResourceLocation id, LiquidSettings liquidSettings) {
		return projection -> SinglePoolElementAccessor.createSinglePoolElement(Either.left(id), StructurePoolElementAccessor.getEMPTY(), projection, Optional.of(liquidSettings));
	}

	public static Function<StructureTemplatePool.Projection, SinglePoolElement> ofProcessedSingle(
			ResourceLocation id, Holder<StructureProcessorList> processorListEntry, LiquidSettings liquidSettings
	) {
		return projection -> SinglePoolElementAccessor.createSinglePoolElement(Either.left(id), processorListEntry, projection, Optional.of(liquidSettings));
	}
}
