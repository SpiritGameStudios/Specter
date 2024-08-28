package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.registry.registration.BlockRegistrar;
import net.minecraft.block.Block;

@SuppressWarnings("unused")
public class SpecterRegistryTestBlockRegistrar implements BlockRegistrar {
	public static final Block TEST_BLOCK = new Block(Block.Settings.copy(net.minecraft.block.Blocks.STONE));
}
