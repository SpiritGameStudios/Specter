package dev.spiritstudios.specter.api.render.client.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public abstract class BlockModelBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	protected final BlockRenderManager renderManager;

	protected BlockModelBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.renderManager = context.getRenderManager();
	}

	@Override
	public void render(T entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
		renderBlockModel(entity, matrices, vertexConsumers, light, overlay);
	}

	protected void renderBlockModel(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		renderManager.renderBlockAsEntity(
				entity.getCachedState(),
				matrices,
				vertexConsumers,
				light, overlay,
				entity.getWorld(), entity.getPos()
		);
	}
}
