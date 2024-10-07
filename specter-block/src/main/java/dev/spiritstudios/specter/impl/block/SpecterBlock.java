package dev.spiritstudios.specter.impl.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.spiritstudios.specter.api.block.BlockMetatags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;

public class SpecterBlock implements ModInitializer {
	public static final BiMap<Block, Block> UNWAXED_TO_WAXED_BLOCKS = HashBiMap.create();
	public static final BiMap<Block, Block> WAXED_TO_UNWAXED_BLOCKS = HashBiMap.create();

	public static final BiMap<Block, Block> OXIDATION_LEVEL_INCREASES = HashBiMap.create();
	public static final BiMap<Block, Block> OXIDATION_LEVEL_DECREASES = HashBiMap.create();


	@Override
	public void onInitialize() {
		BlockMetatags.init();

		reloadBiMaps();
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> reloadBiMaps());
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> reloadBiMaps());
	}
	
	private void reloadBiMaps() {
		UNWAXED_TO_WAXED_BLOCKS.clear();
		WAXED_TO_UNWAXED_BLOCKS.clear();

		BlockMetatags.WAXABLE.forEach((entry) -> {
			UNWAXED_TO_WAXED_BLOCKS.put(entry.key(), entry.value());
			WAXED_TO_UNWAXED_BLOCKS.put(entry.value(), entry.key());
		});

		OXIDATION_LEVEL_INCREASES.clear();
		OXIDATION_LEVEL_DECREASES.clear();

		BlockMetatags.OXIDIZABLE.forEach((entry) -> {
			OXIDATION_LEVEL_INCREASES.put(entry.key(), entry.value());
			OXIDATION_LEVEL_DECREASES.put(entry.value(), entry.key());
		});
	}
}
