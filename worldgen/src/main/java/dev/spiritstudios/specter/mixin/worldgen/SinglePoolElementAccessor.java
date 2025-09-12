package dev.spiritstudios.specter.mixin.worldgen;

import java.util.Optional;

import com.mojang.datafixers.util.Either;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;

@Mixin(SinglePoolElement.class)
public interface SinglePoolElementAccessor {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Invoker("<init>")
	static SinglePoolElement createSinglePoolElement(
		Either<Identifier, StructureTemplate> location,
		RegistryEntry<StructureProcessorList> processors,
		StructurePool.Projection projection,
		Optional<StructureLiquidSettings> overrideLiquidSettings
	) {
		throw new UnsupportedOperationException();
	}
}
