package dev.spiritstudios.specter.impl.config.gui.widget;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FloatSliderWidget extends SliderWidget {
	private final Supplier<Float> getter;
	private final Consumer<Float> setter;

	private final float min;
	private final float max;

	public FloatSliderWidget(String translationKey, float min, float max, Supplier<Float> getter, Consumer<Float> setter) {
		super(0, 0, 0, 20, Text.translatable(translationKey), 0);
		this.getter = getter;
		this.setter = setter;

		Text tooltip = Text.translatableWithFallback(translationKey + ".tooltip", "");
		if (!tooltip.getString().isEmpty()) this.setTooltip(Tooltip.of(tooltip));

		this.min = min;
		this.max = max;

		this.value = (getter.get() - min) / (max - min);
		applyValue();
	}

	@Override
	protected void updateMessage() {
	}

	@Override
	public Text getMessage() {
		return Text.of(super.getMessage().getString()
			+ ": "
			+ String.format("%.1f", getter.get())
		);
	}

	@Override
	protected void applyValue() {
		value = MathHelper.clamp(value, 0, 1);
		setter.accept((float) (value * (max - min) + min));
	}
}
