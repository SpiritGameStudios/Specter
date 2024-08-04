package dev.spiritstudios.specter.impl.config.gui.widget;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntegerSliderWidget extends SliderWidget {
	private final Supplier<Integer> getter;
	private final Consumer<Integer> setter;

	private final int min;
	private final int max;

	public IntegerSliderWidget(String translationKey, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
		super(0, 0, 0, 20, Text.translatable(translationKey), 0);
		this.getter = getter;
		this.setter = setter;

		Text tooltip = Text.translatableWithFallback(translationKey + ".tooltip", "");
		if (!tooltip.getString().isEmpty()) this.setTooltip(Tooltip.of(tooltip));

		this.min = min;
		this.max = max;

		this.value = (double) (getter.get() - min) / (max - min);
		applyValue();
	}

	@Override
	protected void updateMessage() {
	}

	@Override
	public Text getMessage() {
		return Text.of(super.getMessage().getString()
			+ ": "
			+ getter.get()
		);
	}

	@Override
	protected void applyValue() {
		this.value = MathHelper.clamp(value, 0, 1.0);
		setter.accept((int) Math.round(this.value * (max - min) + min));
	}
}
