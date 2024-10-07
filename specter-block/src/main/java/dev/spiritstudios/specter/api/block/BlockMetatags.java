package dev.spiritstudios.specter.api.block;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

public final class BlockMetatags {
	/**
	 * A metatag that specifies that a block is strippable.
	 *
	 * @implNote Note that this metatag does not include blocks made strippable by vanilla or blocks that are registered as strippable using {@link net.fabricmc.fabric.api.registry.StrippableBlockRegistry StrippableBlockRegistry}
	 */
	public static final Metatag<Block, Block> STRIPPABLE = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "strippable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	/**
	 * A metatag that specifies that a block is flattenable.
	 *
	 * @implNote This metatag does not include blocks made flattenable by vanilla or blocks that are registered as flattenable using {@link net.fabricmc.fabric.api.registry.FlattenableBlockRegistry FlattenableBlockRegistry}
	 */
	public static final Metatag<Block, BlockState> FLATTENABLE = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "flattenable"),
		BlockState.CODEC,
		PacketCodecs.entryOf(Block.STATE_IDS).cast()
	).build();

	/**
	 * A metatag that specifies that a block is waxable.
	 *
	 * @implNote This metatag does not include blocks made waxable by vanilla or blocks that are registered as waxable using {@link net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry OxidizableBlocksRegistry}
	 */
	public static final Metatag<Block, Block> WAXABLE = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "waxable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	/**
	 * A metatag that specifies that a block is oxidizable.
	 *
	 * @implNote This metatag does not include blocks made oxidizable by vanilla or blocks that are registered as oxidizable using {@link net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry OxidizableBlocksRegistry}
	 */
	public static final Metatag<Block, Block> OXIDIZABLE = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "oxidizable"),
		Registries.BLOCK.getCodec(),
		PacketCodecs.registryValue(RegistryKeys.BLOCK)
	).build();

	/**
	 * A metatag that specifies the flammability of a block.
	 *
	 * @implNote This metatag does not include blocks made flammable by vanilla or blocks that are registered as flammable using {@link net.fabricmc.fabric.api.registry.FlammableBlockRegistry FlammableBlockRegistry}
	 */
	public static final Metatag<Block, FlammableBlockData> FLAMMABLE = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(SpecterGlobals.MODID, "flammable"),
		FlammableBlockData.CODEC,
		FlammableBlockData.PACKET_CODEC
	).build();

	private BlockMetatags() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	/**
	 * Hacky workaround to force class loading.
	 */
	@SuppressWarnings("EmptyMethod")
	@ApiStatus.Internal
	public static void init() {
		// NO-OP
	}
}
