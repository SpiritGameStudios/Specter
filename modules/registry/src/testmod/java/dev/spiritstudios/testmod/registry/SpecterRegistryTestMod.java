package dev.spiritstudios.testmod.registry;

import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;

import net.fabricmc.api.ModInitializer;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.core.Specter;

public class SpecterRegistryTestMod implements ModInitializer {
	public static final Metatag<Block, Integer> TEST_METATAG = Metatag.builder(
			RegistryKeys.BLOCK,
			Specter.id("test_metatag"),
			Codec.INT
	).packetCodec(PacketCodecs.INTEGER.cast()).build();

	@Override
	public void onInitialize() {

	}
}
