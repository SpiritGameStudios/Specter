package dev.spiritstudios.specter.api.render.client.block;

import java.util.Objects;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public abstract class BlockModelBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	protected final BlockRenderManager renderManager;

	protected BlockModelBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.renderManager = context.getRenderManager();
	}

	protected void renderBlockModel(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		renderManager.getModelRenderer().render(
				entity.getWorld(),
				renderManager.getModel(entity.getCachedState()),
				entity.getCachedState(),
				entity.getPos(),
				matrices,
				vertexConsumers.getBuffer(RenderLayers.getBlockLayer(entity.getCachedState())),
				true,
				Objects.requireNonNull(entity.getWorld()).getRandom(),
				light,
				overlay
		);
	}
}
