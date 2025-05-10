package dev.spiritstudios.specter.api.gui.client.bridget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class TextBridget extends Bridget {
	protected Text content = Text.empty();
	protected boolean shadow = false;
	protected int color = 0xFFFFFFFF;

	public TextBridget() {
		height = client.textRenderer.fontHeight;
	}

	public TextBridget content(Text content) {
		this.content = content;
		width = client.textRenderer.getWidth(content);
		return this;
	}

	public TextBridget content(String content) {
		this.content = Text.literal(content);
		width = client.textRenderer.getWidth(content);
		return this;
	}

	public TextBridget shadow(boolean shadow) {
		this.shadow = shadow;
		return this;
	}

	public TextBridget color(int color) {
		this.color = color;
		return this;
	}

	// Builder

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.drawText(
				client.textRenderer,
				content,
				x(),
				y(),
				color,
				shadow
		);

		super.render(context, mouseX, mouseY, delta);
	}
}
