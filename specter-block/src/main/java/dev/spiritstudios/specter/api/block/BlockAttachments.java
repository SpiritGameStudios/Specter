package dev.spiritstudios.specter.api.block;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class BlockAttachments {
	public static final Attachment<Block, Block> STRIPPABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "strippable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	public static final Attachment<Block, BlockState> FLATTENABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "flattenable"),
		BlockState.CODEC,
		PacketCodecs.entryOf(Block.STATE_IDS).cast()
	).build();

	public static final Attachment<Block, Block> WAXABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "waxable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	public static final Attachment<Block, Block> OXIDIZABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "oxidizable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	public static final Attachment<Block, FlammableBlockData> FLAMMABLE = Attachment.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "flammable"),
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
