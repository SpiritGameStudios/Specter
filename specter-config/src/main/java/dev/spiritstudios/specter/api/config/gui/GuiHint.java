package dev.spiritstudios.specter.api.config.gui;

public interface GuiHint<T> {
	static GuiHint<Integer> slider(int step) {
		return new IntSliderHint(step);
	}

	static GuiHint<Integer> slider() {
		return new IntSliderHint(1);
	}
}
