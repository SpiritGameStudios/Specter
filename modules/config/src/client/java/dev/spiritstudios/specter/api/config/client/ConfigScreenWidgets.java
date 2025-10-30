package dev.spiritstudios.specter.api.config.client;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import dev.spiritstudios.specter.api.config.NumericValue;
import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.core.collect.PatternMap;
import dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget;
import dev.spiritstudios.specter.api.gui.client.widget.SpecterSliderWidget;

@SuppressWarnings("unchecked")
public final class ConfigScreenWidgets {
	private static final PatternMap<BiFunction<Value<?>, String, ? extends AbstractWidget>> widgetFactories = new PatternMap<>();
	private static final BiFunction<Value<?>, String, ? extends AbstractWidget> BOOLEAN_WIDGET_FACTORY = (configValue, id) -> {
		Value<Boolean> value = (Value<Boolean>) configValue;

		return SpecterButtonWidget.builder(
				() -> Component.translatable(value.translationKey(id)).append(": ").append(CommonComponents.optionStatus(value.get())),
				button -> value.set(!value.get())
		).build();
	};
	private static final BiFunction<Value<?>, String, ? extends AbstractWidget> INTEGER_WIDGET_FACTORY = (configValue, id) -> {
		NumericValue<Integer> value = (NumericValue<Integer>) configValue;

		return SpecterSliderWidget.builder(value.get())
				.message((val) -> Component.translatable(value.translationKey(id)).append(String.format(": %.0f", val)))
				.range(value.min(), value.max())
				.step(value.step() == 0 ? 1 : value.step())
				.onValueChanged((val) -> value.set((int) val))
				.build();
	};
	private static final BiFunction<Value<?>, String, ? extends AbstractWidget> DOUBLE_WIDGET_FACTORY = (configValue, id) -> {
		NumericValue<Double> value = (NumericValue<Double>) configValue;

		return SpecterSliderWidget.builder(value.get())
				.message((val) -> Component.translatable(value.translationKey(id)).append(String.format(": %.2f", val)))
				.range(value.min(), value.max())
				.step(value.step())
				.onValueChanged(value::set)
				.build();
	};
	private static final BiFunction<Value<?>, String, ? extends AbstractWidget> FLOAT_WIDGET_FACTORY = (configValue, id) -> {
		NumericValue<Float> value = (NumericValue<Float>) configValue;

		return SpecterSliderWidget.builder(value.get())
				.message((val) -> Component.translatable(configValue.translationKey(id)).append(String.format(": %.1f", val)))
				.range(value.min(), value.max())
				.step(value.step())
				.onValueChanged((val) -> value.set((float) val))
				.build();
	};
	private static final BiFunction<Value<?>, String, ? extends AbstractWidget> STRING_WIDGET_FACTORY = (configValue, id) -> {
		Value<String> value = (Value<String>) configValue;

		EditBox widget = new EditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.nullToEmpty(value.get()));
		widget.setHint(Component.translatableWithFallback("%s.placeholder".formatted(configValue.translationKey(id)), "").withStyle(ChatFormatting.DARK_GRAY));

		widget.setValue(value.get());
		widget.setResponder(value::set);
		widget.setHighlightPos(0);
		widget.setCursorPosition(0);

		return widget;
	};
	private static final BiFunction<Value<?>, String, ? extends AbstractWidget> ENUM_WIDGET_FACTORY = (configValue, id) -> {
		Value<Enum<?>> value = (Value<Enum<?>>) configValue;

		List<Enum<?>> enumValues = Arrays.stream(configValue.defaultValue().getClass().getEnumConstants())
				.filter(val -> val instanceof Enum<?>)
				.map(val -> (Enum<?>) val)
				.collect(Collectors.toList());

		if (enumValues.isEmpty()) throw new IllegalArgumentException("Enum values cannot be null");

		return SpecterButtonWidget.builder(
				() -> Component.translatable(configValue.translationKey(id)).append(": ").append(Component.translatable("%s.%s".formatted(configValue.translationKey(id), value.get().toString().toLowerCase()))),
				button -> {
					Enum<?> current = value.get();
					int index = enumValues.indexOf(current);
					value.set(enumValues.get((index + 1) % enumValues.size()));
				}
		).build();
	};

	static {
		addRegistry(Item.class, BuiltInRegistries.ITEM);
		addRegistry(Block.class, BuiltInRegistries.BLOCK);
	}

	private ConfigScreenWidgets() {
	}

	public static <T> void addRegistry(Class<T> clazz, Registry<T> registry) {
		widgetFactories.put(clazz, (configValue, id) -> {
			Value<T> value = (Value<T>) configValue;

			EditBox widget = new EditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.literal(registry.wrapAsHolder(value.get()).getRegisteredName()));
			widget.setHint(Component.translatableWithFallback("%s.placeholder".formatted(configValue.translationKey(id)), "").withStyle(ChatFormatting.DARK_GRAY));

			widget.setValue(value.get().toString());
			widget.setResponder(val -> {
				ResourceLocation identifier = ResourceLocation.tryParse(val);
				if (identifier == null) return;

				registry.getOptional(identifier).ifPresent(value::set);
			});
			widget.setHighlightPos(0);
			widget.setCursorPosition(0);

			return widget;
		});
	}

	public static void add(Class<?> clazz, BiFunction<Value<?>, String, ? extends AbstractWidget> factory) {
		widgetFactories.put(clazz, factory);
	}

	@ApiStatus.Internal
	public static <T> BiFunction<Value<?>, String, ? extends AbstractWidget> getWidgetFactory(Value<T> value) {
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
