package dev.spiritstudios.specter.api.gui.client.bridget;

import net.minecraft.client.gui.DrawContext;

public class SolidColorBridget extends Bridget {
	protected int color = 0xFFFFFFFF;

	public SolidColorBridget color(int color) {
		this.color = color;
		return this;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.fill(x(), y(), x() + width, y() + height, color);

		super.render(context, mouseX, mouseY, delta);
	}
}
