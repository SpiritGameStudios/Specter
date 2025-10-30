package dev.spiritstudios.testmod.registry;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.Block;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.core.Specter;

public class SpecterRegistryTestMod implements ModInitializer {
	public static final Metatag<Block, Integer> TEST_METATAG = Metatag.builder(
			Registries.BLOCK,
			Specter.id("test_metatag"),
			Codec.INT
	).packetCodec(ByteBufCodecs.INT.cast()).build();

	@Override
	public void onInitialize() {

	}
}
