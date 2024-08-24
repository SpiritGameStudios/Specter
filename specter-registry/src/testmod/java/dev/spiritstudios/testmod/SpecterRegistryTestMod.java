package dev.spiritstudios.testmod;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;

import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.minecraft.util.Identifier;

public class SpecterRegistryTestMod implements ModInitializer {

	public static final String MOD_ID = "specter-registry-testmod";
	public static final Identifier ATTACHMENT_ID = Identifier.of(MOD_ID, "attachment_test");
	public static Attachment<Block, Integer> TEST_ATTACHMENT;

	@Override
	public void onInitialize() {
		ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "registry-id"), SpecterRegistryTestCommand.RegistryArgumentType.class, ConstantArgumentSerializer.of(IdentifierArgumentType::identifier));
		ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "attachment-id"), SpecterRegistryTestCommand.AttachmentArgumentType.class, ConstantArgumentSerializer.of(IdentifierArgumentType::identifier));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			SpecterRegistryTestCommand.register(dispatcher, registryAccess);
		});

		TEST_ATTACHMENT = Attachment.builder(
				Registries.BLOCK,
				ATTACHMENT_ID,
				Codec.INT,
				PacketCodecs.INTEGER.cast()
		).build();
		TEST_ATTACHMENT.put(Blocks.BRICK_SLAB, 2);
		TEST_ATTACHMENT.put(Blocks.STICKY_PISTON, 69);
	}

}
