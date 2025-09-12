package dev.spiritstudios.specter.impl.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.block.Block;

import net.fabricmc.api.ModInitializer;

import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.api.registry.metatag.MetatagEvents;


public class SpecterBlock implements ModInitializer {
	public static final Object2ObjectOpenHashMap<Block, Block> WAXED_TO_UNWAXED_BLOCKS =  new Object2ObjectOpenHashMap<>();
	public static final Object2ObjectOpenHashMap<Block, Block> OXIDATION_LEVEL_DECREASES = new Object2ObjectOpenHashMap<>();

	@Override
	public void onInitialize() {
		BlockMetatags.init();

		MetatagEvents.metatagLoadedEvent(BlockMetatags.WAXABLE).register(resourceManager -> {
			WAXED_TO_UNWAXED_BLOCKS.clear();

			BlockMetatags.WAXABLE.forEach((entry) -> {
				WAXED_TO_UNWAXED_BLOCKS.put(entry.getValue(), entry.getKey());
			});

			WAXED_TO_UNWAXED_BLOCKS.trim();
		});

		MetatagEvents.metatagLoadedEvent(BlockMetatags.OXIDIZABLE).register(resourceManager -> {
			OXIDATION_LEVEL_DECREASES.clear();

			BlockMetatags.OXIDIZABLE.forEach((entry) -> {
				OXIDATION_LEVEL_DECREASES.put(entry.getValue(), entry.getKey());
			});

			OXIDATION_LEVEL_DECREASES.trim();
		});
	}
}
