package dev.spiritstudios.specter.api.block;

import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static dev.spiritstudios.specter.impl.base.Specter.MODID;

public final class BlockAttachments {
	public static final Attachment<Block, Block> STRIPPABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(MODID, "strippable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	public static final Attachment<Block, BlockState> FLATTENABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(MODID, "flattenable"),
		BlockState.CODEC,
		PacketCodecs.entryOf(Block.STATE_IDS).cast()
	).build();

	public static final Attachment<Block, Block> WAXABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(MODID, "waxable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	public static final Attachment<Block, Block> OXIDIZABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(MODID, "oxidizable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	public static final Attachment<Block, FlammableBlockData> FLAMMABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(MODID, "flammable"),
		FlammableBlockData.CODEC,
		FlammableBlockData.PACKET_CODEC
	).build();

	/**
	 * Hacky workaround to force class loading.
	 */
	@SuppressWarnings("EmptyMethod")
	public static void init() {
		// NO-OP
	}
}
