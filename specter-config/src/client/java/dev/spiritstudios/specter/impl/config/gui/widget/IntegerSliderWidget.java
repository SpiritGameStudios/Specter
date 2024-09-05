package dev.spiritstudios.specter.impl.config.gui.widget;

import com.mojang.datafixers.util.Pair;
import dev.spiritstudios.specter.api.config.Config;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class IntegerSliderWidget extends SliderWidget {
	private final Config.Value<Integer> configValue;

	private final int min;
	private final int max;

	public IntegerSliderWidget(Config.Value<Integer> configValue, Identifier configId) {
		super(0, 0, 0, 20, Text.translatable(configValue.translationKey(configId)), 0);
		this.configValue = configValue;

		Text tooltip = Text.translatableWithFallback("%s.tooltip".formatted(configValue.translationKey(configId)), "");
		if (!tooltip.getString().isEmpty()) this.setTooltip(Tooltip.of(tooltip));

		Pair<Integer, Integer> range = configValue.range();
		this.min = range == null ? 0 : range.getFirst();
		this.max = range == null ? 100 : range.getSecond();

		this.value = configValue.get();
		applyValue();
	}

	@Override
	protected void updateMessage() {
	}

	@Override
	public Text getMessage() {
		return Text.of("%s: %d".formatted(super.getMessage().getString(), configValue.get()));
	}

	@Override
	protected void applyValue() {
		this.value = MathHelper.clamp(value, 0, 1.0);
		configValue.set((int) Math.round(this.value * (max - min) + min));
	}
}
