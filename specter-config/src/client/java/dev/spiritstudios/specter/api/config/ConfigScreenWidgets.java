package dev.spiritstudios.specter.api.config;

import dev.spiritstudios.specter.api.gui.widget.SpecterButtonWidget;
import dev.spiritstudios.specter.api.gui.widget.SpecterSliderWidget;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public final class ConfigScreenWidgets {
	private static final Map<Class<?>, BiFunction<Value<?>, Identifier, ? extends ClickableWidget>> widgetFactories = new Object2ObjectOpenHashMap<>();

	private static final BiFunction<Value<?>, Identifier, ? extends ClickableWidget> BOOLEAN_WIDGET_FACTORY = (configValue, id) -> {
		Value<Boolean> value = (Value<Boolean>) configValue;

		return SpecterButtonWidget.builder(
			() -> Text.translatable(value.translationKey(id)).append(": ").append(ScreenTexts.onOrOff(value.get())),
			button -> value.set(!value.get())
		).build();
	};

	private static final BiFunction<Value<?>, Identifier, ? extends ClickableWidget> INTEGER_WIDGET_FACTORY = (configValue, id) -> {
		NumericValue<Integer> value = (NumericValue<Integer>) configValue;

		return SpecterSliderWidget.builder(MathHelper.map(value.get(), value.range().min(), value.range().max(), 0.0, 1.0))
			.message((val) -> Text.translatable(value.translationKey(id)).append(String.format(": %.0f", val)))
			.range(value.range().min(), value.range().max())
			.step(value.step() == 0 ? 1 : value.step())
			.onValueChanged((val) -> value.set(val.intValue()))
			.build();
	};

	private static final BiFunction<Value<?>, Identifier, ? extends ClickableWidget> DOUBLE_WIDGET_FACTORY = (configValue, id) -> {
		NumericValue<Double> value = (NumericValue<Double>) configValue;

		return SpecterSliderWidget.builder(MathHelper.map(value.get(), value.range().min(), value.range().max(), 0.0, 1.0))
			.message((val) -> Text.translatable(value.translationKey(id)).append(String.format(": %.2f", val)))
			.range(value.range())
			.step(value.step())
			.onValueChanged(value::set)
			.build();
	};

	private static final BiFunction<Value<?>, Identifier, ? extends ClickableWidget> FLOAT_WIDGET_FACTORY = (configValue, id) -> {
		NumericValue<Float> value = (NumericValue<Float>) configValue;

		return SpecterSliderWidget.builder(MathHelper.map(value.get(), value.range().min(), value.range().max(), 0.0, 1.0))
			.message((val) -> Text.translatable(configValue.translationKey(id)).append(String.format(": %.1f", val)))
			.range(value.range().min(), value.range().max())
			.step(value.step())
			.onValueChanged((val) -> value.set(val.floatValue()))
			.build();
	};

	private static final BiFunction<Value<?>, Identifier, ? extends ClickableWidget> STRING_WIDGET_FACTORY = (configValue, id) -> {
		Value<String> value = (Value<String>) configValue;

		TextFieldWidget widget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 0, 0, Text.of(value.get()));
		widget.setPlaceholder(Text.translatableWithFallback("%s.placeholder".formatted(configValue.translationKey(id)), "").formatted(Formatting.DARK_GRAY));

		widget.setText(value.get());
		widget.setChangedListener(value::set);
		widget.setSelectionEnd(0);
		widget.setSelectionStart(0);

		return widget;
	};

	private static final BiFunction<Value<?>, Identifier, ? extends ClickableWidget> ENUM_WIDGET_FACTORY = (configValue, id) -> {
		Value<Enum<?>> value = (Value<Enum<?>>) configValue;

		List<Enum<?>> enumValues = Arrays.stream(configValue.defaultValue().getClass().getEnumConstants())
			.filter(val -> val instanceof Enum<?>)
			.map(val -> (Enum<?>) val)
			.collect(Collectors.toList());

		if (enumValues.isEmpty()) throw new IllegalArgumentException("Enum values cannot be null");

		return SpecterButtonWidget.builder(
			() -> Text.translatable(configValue.translationKey(id)).append(": ").append(Text.translatable("%s.%s".formatted(configValue.translationKey(id), value.get().toString().toLowerCase()))),
			button -> {
				Enum<?> current = value.get();
				int index = enumValues.indexOf(current);
				value.set(enumValues.get((index + 1) % enumValues.size()));
			}
		).build();
	};

	private ConfigScreenWidgets() {
	}

	public static void add(Class<?> clazz, BiFunction<Value<?>, Identifier, ? extends ClickableWidget> factory) {
		widgetFactories.put(clazz, factory);
	}

	@ApiStatus.Internal
	public static <T> BiFunction<Value<?>, Identifier, ? extends ClickableWidget> getWidgetFactory(Value<T> value) {
		// We are using a switch instead of just adding to our map for 2 reasons:
		// 1. It's (usually) faster than a map lookup, as most of the time the value will be one of these types
		// 2. It lets us handle the lowercased names of primitive types, which are different Class<> instances because reasons
		return switch (value.defaultValue()) {
			case Boolean ignored -> BOOLEAN_WIDGET_FACTORY;
			case Integer ignored -> INTEGER_WIDGET_FACTORY;
			case Double ignored -> DOUBLE_WIDGET_FACTORY;
			case Float ignored -> FLOAT_WIDGET_FACTORY;
			case String ignored -> STRING_WIDGET_FACTORY;
			case Enum<?> ignored -> ENUM_WIDGET_FACTORY;
			default -> widgetFactories.get(value.defaultValue().getClass());
		};
	}
}
