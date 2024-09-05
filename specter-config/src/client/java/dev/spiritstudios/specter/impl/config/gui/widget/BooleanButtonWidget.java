package dev.spiritstudios.specter.impl.config.gui.widget;

import dev.spiritstudios.specter.api.config.Config;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BooleanButtonWidget extends ButtonWidget {
	private final Config.Value<Boolean> configValue;

	public BooleanButtonWidget(Config.Value<Boolean> configValue, Identifier configId) {
		super(
			0,
			0,
			0,
			20,
			Text.translatable(configValue.translationKey(configId)),
			button -> configValue.set(!configValue.get()),
			button -> null
		);

		Text tooltip = Text.translatableWithFallback("%s.tooltip".formatted(configValue.translationKey(configId)), "");
		if (!tooltip.getString().isEmpty()) this.setTooltip(Tooltip.of(tooltip));

		this.configValue = configValue;
	}

	@Override
	public Text getMessage() {
		return Text.literal("%s: %s".formatted(super.getMessage().getString(), ScreenTexts.onOrOff(this.configValue.get()).getString()));
	}
}
