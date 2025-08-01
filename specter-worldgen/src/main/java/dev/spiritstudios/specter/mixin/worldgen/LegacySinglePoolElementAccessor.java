package dev.spiritstudios.specter.mixin.worldgen;

import java.util.Optional;

import com.mojang.datafixers.util.Either;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;

@Mixin(LegacySinglePoolElement.class)
public interface LegacySinglePoolElementAccessor {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Invoker("<init>")
	static LegacySinglePoolElement createLegacySinglePoolElement(
		Either<Identifier, StructureTemplate> either,
		RegistryEntry<StructureProcessorList> registryEntry,
		StructurePool.Projection projection,
		Optional<StructureLiquidSettings> optional
	) {
		throw new UnsupportedOperationException();
	}
}
