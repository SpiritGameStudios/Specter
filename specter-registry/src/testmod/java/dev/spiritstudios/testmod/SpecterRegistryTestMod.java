package dev.spiritstudios.testmod;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class SpecterRegistryTestMod implements ModInitializer {
	public static final String MOD_ID = "specter-registry-testmod";
	public static final Identifier ATTACHMENT_ID = Identifier.of(MOD_ID, "attachment_test");
	public static final Identifier CLIENT_ATTACHMENT_ID = Identifier.of(MOD_ID, "attachment_client_test");
	public static final Attachment<Block, Integer> TEST_ATTACHMENT = Attachment.builder(
		Registries.BLOCK,
		ATTACHMENT_ID,
		Codec.INT,
		PacketCodecs.INTEGER.cast()
	).build();

	public static final Attachment<Block, Integer> TEST_CLIENT_ATTACHMENT = Attachment.builder(
		Registries.BLOCK,
		CLIENT_ATTACHMENT_ID,
		Codec.INT,
		PacketCodecs.INTEGER.cast()
	).side(ResourceType.CLIENT_RESOURCES).build();


	@Override
	public void onInitialize() {

	}

}
