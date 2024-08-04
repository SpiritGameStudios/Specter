package dev.spiritstudios.specter.impl.config.gui.widget;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumButtonWidget extends ButtonWidget {
	private final Supplier<Enum<?>> getter;
	private final Consumer<Enum<?>> setter;

	private final List<Enum<?>> enumValues = new ArrayList<>();

	private final String translationKey;

	public EnumButtonWidget(String translationKey, Supplier<Enum<?>> getter, Consumer<Enum<?>> setter, Enum<?> enumValue) {
		super(
			0,
			0,
			0,
			20,
			Text.translatable(translationKey),
			button -> {
			},
			button -> null
		);

		this.getter = getter;
		this.setter = setter;

		List<?> values = Arrays.asList(enumValue.getClass().getEnumConstants());

		if (values.isEmpty()) throw new IllegalArgumentException("Enum values cannot be null");
		for (Object value : values) if (value instanceof Enum<?>) enumValues.add((Enum<?>) value);

		Text tooltip = Text.translatableWithFallback(translationKey + ".tooltip", "");
		if (!tooltip.getString().isEmpty()) this.setTooltip(Tooltip.of(tooltip));

		this.translationKey = translationKey;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);
		cycle();
	}

	private void cycle() {
		Enum<?> current = getter.get();
		int index = enumValues.indexOf(current);
		setter.accept(enumValues.get((index + 1) % enumValues.size()));
	}

	@Override
	public Text getMessage() {
		return Text.of(super.getMessage().getString()
			+ ": "
			+ Text.translatable(translationKey + "." + getter.get().toString().toLowerCase()).getString()
		);
	}
}
