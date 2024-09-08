package dev.spiritstudios.specter.impl.config.gui.widget;

import dev.spiritstudios.specter.api.config.Config;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumButtonWidget extends ButtonWidget {
	private final Config.Value<Enum<?>> configValue;
	private final Identifier configId;
	private final List<Enum<?>> enumValues = new ArrayList<>();
	
	public EnumButtonWidget(Config.Value<Enum<?>> configValue, Identifier configId) {
		super(
			0,
			0,
			0,
			20,
			Text.translatable(configValue.translationKey(configId)),
			button -> {
			},
			button -> null
		);

		this.configValue = configValue;
		this.configId = configId;
		List<?> values = Arrays.asList(configValue.defaultValue().getClass().getEnumConstants());

		if (values.isEmpty()) throw new IllegalArgumentException("Enum values cannot be null");
		values.stream()
			.filter(value -> value instanceof Enum<?>)
			.map(value -> (Enum<?>) value)
			.forEach(enumValues::add);

		Text tooltip = Text.translatableWithFallback("%s.tooltip".formatted(configValue.translationKey(configId)), "");
		if (!tooltip.getString().isEmpty()) this.setTooltip(Tooltip.of(tooltip));
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);
		cycle();
	}

	private void cycle() {
		Enum<?> current = configValue.get();
		int index = enumValues.indexOf(current);
		configValue.set(enumValues.get((index + 1) % enumValues.size()));
	}

	@Override
	public Text getMessage() {
		return Text.of("%s: %s".formatted(
				super.getMessage().getString(),
				Text.translatable(
					"%s.%s".formatted(
						configValue.translationKey(configId),
						configValue.get().toString().toLowerCase()
					)
				).getString()
			)
		);
	}
}
