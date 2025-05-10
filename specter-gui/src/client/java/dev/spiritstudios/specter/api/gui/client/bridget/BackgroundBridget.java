package dev.spiritstudios.specter.api.gui.client.bridget;

import static net.minecraft.client.gui.screen.Screen.MENU_BACKGROUND_TEXTURE;

import java.util.function.Supplier;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class BackgroundBridget extends Bridget {
	private static final Identifier INWORLD_MENU_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_menu_background.png");

	protected boolean blur = false;
	protected Supplier<Identifier> texture = () ->
			client.world == null ? MENU_BACKGROUND_TEXTURE : INWORLD_MENU_BACKGROUND_TEXTURE;

	public BackgroundBridget blur(boolean blur) {
		this.blur = blur;
		return this;
	}

	public BackgroundBridget texture(Supplier<Identifier> texture) {
		this.texture = texture;
		return this;
	}

	public BackgroundBridget texture(Identifier texture) {
		return texture(() -> texture);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (blur) {
			client.gameRenderer.renderBlur();
			client.getFramebuffer().beginWrite(false);
		}

		context.drawTexture(
				RenderLayer::getGuiTextured,
				texture.get(),
				x(), y(),
				0, 0,
				width, height,
				32, 32
		);

		super.render(context, mouseX, mouseY, delta);
	}
}
