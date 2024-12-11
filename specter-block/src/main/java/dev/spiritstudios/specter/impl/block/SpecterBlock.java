package dev.spiritstudios.specter.impl.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.api.registry.metatag.MetatagEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;

public class SpecterBlock implements ModInitializer {
	public static final BiMap<Block, Block> UNWAXED_TO_WAXED_BLOCKS = HashBiMap.create();
	public static final BiMap<Block, Block> WAXED_TO_UNWAXED_BLOCKS = HashBiMap.create();

	public static final BiMap<Block, Block> OXIDATION_LEVEL_INCREASES = HashBiMap.create();
	public static final BiMap<Block, Block> OXIDATION_LEVEL_DECREASES = HashBiMap.create();


	@Override
	public void onInitialize() {
		BlockMetatags.init();

		MetatagEvents.metatagLoadedEvent(BlockMetatags.WAXABLE).register(resourceManager -> {
			UNWAXED_TO_WAXED_BLOCKS.clear();
			WAXED_TO_UNWAXED_BLOCKS.clear();

			BlockMetatags.WAXABLE.forEach((entry) -> {
				UNWAXED_TO_WAXED_BLOCKS.put(entry.key(), entry.value());
				WAXED_TO_UNWAXED_BLOCKS.put(entry.value(), entry.key());
			});
		});

		MetatagEvents.metatagLoadedEvent(BlockMetatags.OXIDIZABLE).register(resourceManager -> {
			OXIDATION_LEVEL_INCREASES.clear();
			OXIDATION_LEVEL_DECREASES.clear();

			BlockMetatags.OXIDIZABLE.forEach((entry) -> {
				OXIDATION_LEVEL_INCREASES.put(entry.key(), entry.value());
				OXIDATION_LEVEL_DECREASES.put(entry.value(), entry.key());
			});
		});
	}
}
