package dev.spiritstudios.specter.impl.config.gui.widget;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanButtonWidget extends ButtonWidget {
	private final Supplier<Boolean> getter;

	public BooleanButtonWidget(String translationKey, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		super(
			0,
			0,
			0,
			20,
			Text.translatable(translationKey),
			button -> setter.accept(!getter.get()),
			button -> null
		);

		Text tooltip = Text.translatableWithFallback(translationKey + ".tooltip", "");
		if (!tooltip.getString().isEmpty()) this.setTooltip(Tooltip.of(tooltip));

		this.getter = getter;
	}

	@Override
	public Text getMessage() {
		return Text.literal(super.getMessage().getString()
			+ ": "
			+ ScreenTexts.onOrOff(this.getter.get()).getString()
		);
	}
}
