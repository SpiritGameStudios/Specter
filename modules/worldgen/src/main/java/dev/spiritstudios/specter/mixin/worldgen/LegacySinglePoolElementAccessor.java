package dev.spiritstudios.specter.mixin.worldgen;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import com.mojang.datafixers.util.Either;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LegacySinglePoolElement.class)
public interface LegacySinglePoolElementAccessor {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Invoker("<init>")
	static LegacySinglePoolElement createLegacySinglePoolElement(
		Either<ResourceLocation, StructureTemplate> either,
		Holder<StructureProcessorList> registryEntry,
		StructureTemplatePool.Projection projection,
		Optional<LiquidSettings> optional
	) {
		throw new UnsupportedOperationException();
	}
}
