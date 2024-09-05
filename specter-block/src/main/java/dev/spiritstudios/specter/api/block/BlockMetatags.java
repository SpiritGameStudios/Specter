package dev.spiritstudios.specter.api.block;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class BlockMetatags {
	public static final Metatag<Block, Block> STRIPPABLE = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "strippable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	public static final Metatag<Block, BlockState> FLATTENABLE = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "flattenable"),
		BlockState.CODEC,
		PacketCodecs.entryOf(Block.STATE_IDS).cast()
	).build();

	public static final Metatag<Block, Block> WAXABLE = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "waxable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	public static final Metatag<Block, Block> OXIDIZABLE = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "oxidizable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	public static final Metatag<Block, FlammableBlockData> FLAMMABLE = Metatag.builder(
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
