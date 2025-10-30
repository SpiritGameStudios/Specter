package dev.spiritstudios.specter.mixin.worldgen;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import com.mojang.datafixers.util.Either;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SinglePoolElement.class)
public interface SinglePoolElementAccessor {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Invoker("<init>")
	static SinglePoolElement createSinglePoolElement(
		Either<ResourceLocation, StructureTemplate> location,
		Holder<StructureProcessorList> processors,
		StructureTemplatePool.Projection projection,
		Optional<LiquidSettings> overrideLiquidSettings
	) {
		throw new UnsupportedOperationException();
	}
}
