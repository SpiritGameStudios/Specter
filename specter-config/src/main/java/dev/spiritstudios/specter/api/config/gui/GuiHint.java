package dev.spiritstudios.specter.api.config.gui;

import dev.spiritstudios.specter.api.config.Config;

public interface GuiHint<T> {
	static GuiHint<Integer> slider(int step) {
		return new IntSliderHint(step);
	}

	static GuiHint<Integer> slider() {
		return new IntSliderHint(1);
	}

	static GuiHint<Config.SubConfig> tab() {return new SubConfigHints.TabHint();}

	static GuiHint<Config.SubConfig> innerMenu() {return new SubConfigHints.InnerMenuHint();}

	static GuiHint<Config.SubConfig> section() {return new SubConfigHints.SectionHint();}
}
