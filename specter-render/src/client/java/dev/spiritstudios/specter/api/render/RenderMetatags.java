package dev.spiritstudios.specter.api.render;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import static dev.spiritstudios.specter.api.core.SpecterGlobals.MODID;

public final class RenderMetatags {
	public static final Metatag<Block, BlockRenderLayer> RENDER_LAYER = Metatag.builder(
		Registries.BLOCK,
		Identifier.of(MODID, "render_layer"),
		BlockRenderLayer.CODEC,
		BlockRenderLayer.PACKET_CODEC.cast()
	).side(ResourceType.CLIENT_RESOURCES).build();

	/**
	 * Hacky workaround to force class loading.
	 */
	@SuppressWarnings("EmptyMethod")
	public static void init() {
		// NO-OP
	}

	private RenderMetatags() {
	}
}
