package dev.spiritstudios.testmod;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.api.registry.registration.Registrar;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class SpecterRegistryTestMod implements ModInitializer {

	public static final String MOD_ID = "specter-registry-testmod";
	public static final Identifier ATTACHMENT_ID = Identifier.of(MOD_ID, "attachment_test");
	public static final Attachment<Block, Integer> TEST_ATTACHMENT = Attachment.builder(
		Registries.BLOCK,
		ATTACHMENT_ID,
		Codec.INT,
		PacketCodecs.INTEGER.cast()
	).build();

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			SpecterRegistryTestCommand.register(dispatcher);
		});

		Registrar.process(SpecterRegistryTestBlockRegistrar.class, "specter-registry-testmod");
	}

}
