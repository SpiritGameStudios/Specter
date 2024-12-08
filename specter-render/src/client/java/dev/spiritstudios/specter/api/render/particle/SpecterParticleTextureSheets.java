package dev.spiritstudios.specter.api.render.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public final class SpecterParticleTextureSheets {
	public static final ParticleTextureSheet PARTICLE_SHEET_ADDITIVE = new ParticleTextureSheet() {
		@Override
		public @Nullable BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
			RenderSystem.enableDepthTest();
			RenderSystem.setShader(GameRenderer::getParticleProgram);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
			return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		}

		public String toString() {
			return "PARTICLE_SHEET_ADDITIVE";
		}
	};
}
