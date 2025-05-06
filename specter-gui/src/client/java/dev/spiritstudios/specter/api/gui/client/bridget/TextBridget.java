package dev.spiritstudios.specter.api.gui.client.bridget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class TextBridget extends ClickableBridget {
	private final MinecraftClient client = MinecraftClient.getInstance();
	private Text content;
	private boolean shadow;
	private int colour;

	public TextBridget(int x, int y, int width, int height, Text content, int colour, boolean shadow, boolean overrideDimensions, boolean mayContain) {
		super(x, y, width, height, overrideDimensions, mayContain);

		this.shadow = shadow;
		this.colour = colour;
		this.content = content;
	}

	// Content

	public void setContent(Text content) {
		this.content = content;
	}

	public Text getContent() {
		return this.content;
	}

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

	public boolean getShadow() {
		return this.shadow;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public int getColour() {
		return this.colour;
	}

	// Builder

	public static class Builder extends Bridget.Builder {
		private Text content;
		private int colour = 0xFFFFFF;
		private boolean shadow = true;
		public boolean mayContainChildren = true;

		public Builder setContent(Text text) {
			this.content = text;
			return this;
		}

		public Builder setColour(int colour) {
			this.colour = colour;
			return this;
		}

		public Builder setShadow(boolean shadow) {
			this.shadow = shadow;
			return this;
		}

		@Override
		public TextBridget build() {
			return new TextBridget(this.x, this.y, this.width, this.height, this.content, this.colour, this.shadow, this.overrideDimensions, this.mayContainChildren);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.drawText(client.textRenderer, this.content, this.position.x, this.position.y, this.colour, this.shadow);
	}
}
