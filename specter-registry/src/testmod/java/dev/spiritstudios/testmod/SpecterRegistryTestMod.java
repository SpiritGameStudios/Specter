package dev.spiritstudios.testmod;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.registry.RegistryHelper;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class SpecterRegistryTestMod implements ModInitializer {
	public static final String MODID = "specter-registry-testmod";
	public static final Identifier METATAG_ID = Identifier.of(MODID, "metatag_test");
	public static final Metatag<Block, Integer> TEST_METATAG = Metatag.builder(
		Registries.BLOCK,
		METATAG_ID,
		Codec.INT,
		PacketCodecs.INTEGER.cast()
	).build();
	public static final Identifier CLIENT_METATAG_ID = Identifier.of(MODID, "metatag_client_test");
	public static final Metatag<Block, Integer> TEST_CLIENT_METATAG = Metatag.builder(
		Registries.BLOCK,
		CLIENT_METATAG_ID,
		Codec.INT,
		PacketCodecs.INTEGER.cast()
	).side(ResourceType.CLIENT_RESOURCES).build();

	@Override
	public void onInitialize() {
		RegistryHelper.registerBlocks(SpecterRegistryTestBlocks.class, MODID);
	}
}
