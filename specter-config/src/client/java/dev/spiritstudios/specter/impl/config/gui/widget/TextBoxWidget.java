package dev.spiritstudios.specter.impl.config.gui.widget;

import dev.spiritstudios.specter.api.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TextBoxWidget extends TextFieldWidget {
	private final Config.Value<String> configValue;

	public TextBoxWidget(Config.Value<String> configValue, Identifier configId) {
		super(MinecraftClient.getInstance().textRenderer, 0, 0, 0, 20, Text.of(configValue.get()));
		this.configValue = configValue;

		setPlaceholder(Text.translatableWithFallback("%s.placeholder".formatted(configValue.translationKey(configId)), "").formatted(Formatting.DARK_GRAY));

		Text tooltip = Text.translatableWithFallback("%s.tooltip".formatted(configValue.translationKey(configId)), "");
		if (!tooltip.getString().isEmpty()) this.setTooltip(Tooltip.of(tooltip));

		this.setText(configValue.get());
		setSelectionEnd(0);
		setSelectionStart(0);
	}

	@Override
	public void write(String text) {
		super.write(text);
		this.configValue.set(this.getText());
	}

	@Override
	public void eraseCharacters(int characterOffset) {
		super.eraseCharacters(characterOffset);
		this.configValue.set(this.getText());
	}

	@Override
	public void eraseWords(int wordOffset) {
		super.eraseWords(wordOffset);
		this.configValue.set(this.getText());
	}
}
