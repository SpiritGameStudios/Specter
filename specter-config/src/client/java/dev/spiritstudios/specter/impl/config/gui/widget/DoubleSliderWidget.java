package dev.spiritstudios.specter.impl.config.gui.widget;

import com.mojang.datafixers.util.Pair;
import dev.spiritstudios.specter.api.config.Config;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class DoubleSliderWidget extends SliderWidget {
	private final Config.Value<Double> configValue;

	private final double min;
	private final double max;

	public DoubleSliderWidget(Config.Value<Double> configValue, Identifier configId) {
		super(0, 0, 0, 20, Text.translatable(configValue.translationKey(configId)), 0);
		this.configValue = configValue;

		Text tooltip = Text.translatableWithFallback("%s.tooltip".formatted(configValue.translationKey(configId)), "");
		if (!tooltip.getString().isEmpty()) this.setTooltip(Tooltip.of(tooltip));

		Pair<Double, Double> range = configValue.range();
		this.min = range == null ? 0.0D : range.getFirst();
		this.max = range == null ? 1.0D : range.getSecond();

		this.value = configValue.get();
		applyValue();
	}


	@Override
	protected void updateMessage() {
	}

	@Override
	public Text getMessage() {
		return Text.of("%s: %s".formatted(super.getMessage().getString(), String.format("%.2f", configValue.get())));
	}

	@Override
	protected void applyValue() {
		value = MathHelper.clamp(value, 0, 1);
		configValue.set(value * (max - min) + min);
	}
}
