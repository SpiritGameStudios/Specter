package dev.spiritstudios.specter.api.block;

import org.jetbrains.annotations.ApiStatus;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.core.Specter;
import dev.spiritstudios.specter.mixin.block.AxeItemAccessor;
import dev.spiritstudios.specter.mixin.block.ShovelItemAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockMetatags {
	/**
	 * A metatag that specifies that a block is strippable.
	 */
	public static final Metatag<Block, Block> STRIPPABLE = Metatag.builder(
					Registries.BLOCK,
					Specter.id("strippable"),
					BuiltInRegistries.BLOCK.byNameCodec()
			)
			.packetCodec(ByteBufCodecs.registry(Registries.BLOCK))
			.existingCombined(AxeItemAccessor::getSTRIPPABLES).build();

	/**
	 * A metatag that specifies that a block is flattenable.
	 */
	public static final Metatag<Block, BlockState> FLATTENABLE = Metatag.builder(
					Registries.BLOCK,
					Specter.id("flattenable"),
					BlockState.CODEC
			)
			.packetCodec(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY).cast())
			.existingCombined(ShovelItemAccessor::getFLATTENABLES)
			.build();

	/**
	 * A metatag that specifies that a block is waxable.
	 *
	 * @implNote This metatag does not include blocks made waxable by vanilla or blocks that are registered as waxable using {@link net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry OxidizableBlocksRegistry}
	 */
	public static final Metatag<Block, Block> WAXABLE = Metatag.builder(
					Registries.BLOCK,
					Specter.id("waxable"),
					BuiltInRegistries.BLOCK.byNameCodec()
			)
			.packetCodec(ByteBufCodecs.registry(Registries.BLOCK))
			.build();

	/**
	 * A metatag that specifies that a block is oxidizable.
	 *
	 * @implNote This metatag does not include blocks made oxidizable by vanilla or blocks that are registered as oxidizable using {@link net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry OxidizableBlocksRegistry}
	 */
	public static final Metatag<Block, Block> OXIDIZABLE = Metatag.builder(
					Registries.BLOCK,
					Specter.id("oxidizable"),
					BuiltInRegistries.BLOCK.byNameCodec()
			)
			.packetCodec(ByteBufCodecs.registry(Registries.BLOCK))
			.build();

	/**
	 * A metatag that specifies the flammability of a block.
	 *
	 * @implNote Due to a limitation of the fabric api, blocks registered as flammable using {@link net.fabricmc.fabric.api.registry.FlammableBlockRegistry FlammableBlockRegistry}, by vanilla, or using other means, are not included in this metatag.
	 */
	public static final Metatag<Block, FlammableBlockData> FLAMMABLE = Metatag.builder(
					Registries.BLOCK,
					Specter.id("flammable"),
					FlammableBlockData.CODEC
			)
			.packetCodec(FlammableBlockData.PACKET_CODEC)
			.build();

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
