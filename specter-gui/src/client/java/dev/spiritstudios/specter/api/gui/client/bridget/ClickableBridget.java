package dev.spiritstudios.specter.api.gui.client.bridget;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

public class ClickableBridget extends Bridget implements Element, Selectable {
	public ClickableBridget(int x, int y, int width, int height, boolean overrideDimensions, boolean mayContain) {
		super(x, y, width, height, overrideDimensions, mayContain);
	}

	// Clickable / Element methods

	@Override
	public void setFocused(boolean focused) {
	}

	@Override
	public boolean isFocused() {
		return false;
	}

	@Override
	public SelectionType getType() {
		return null;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
	}
}
