package dev.spiritstudios.specter.impl.core.debug;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;

public final class ToggleableDebugRenderer implements DebugRenderer.Renderer {
	private final DebugRenderer.Renderer renderer;
	private boolean enabled;

	public ToggleableDebugRenderer(DebugRenderer.Renderer renderer) {
		this.renderer = renderer;
	}

	public void toggle() {
		this.enabled = !enabled;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		if (this.enabled) renderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
	}
}
