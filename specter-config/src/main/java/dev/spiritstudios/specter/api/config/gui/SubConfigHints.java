package dev.spiritstudios.specter.api.config.gui;

import dev.spiritstudios.specter.api.config.Config;

public class SubConfigHints {
	public record InnerMenuHint() implements GuiHint<Config.SubConfig> {
	}

	public record TabHint() implements GuiHint<Config.SubConfig> {
	}

	public record SectionHint() implements GuiHint<Config.SubConfig> {
	}
}
