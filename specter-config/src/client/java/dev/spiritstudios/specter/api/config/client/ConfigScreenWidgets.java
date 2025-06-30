package dev.spiritstudios.specter.api.config.client;

import java.util.function.BiFunction;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.widget.ClickableWidget;

import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.core.collect.PatternMap;

@SuppressWarnings("unchecked")
public final class ConfigScreenWidgets {
	private static final PatternMap<BiFunction<Value<?>, String, ? extends ClickableWidget>> widgetFactories = new PatternMap<>();


	public static void add(Class<?> clazz, BiFunction<Value<?>, String, ? extends ClickableWidget> factory) {
		widgetFactories.put(clazz, factory);
	}

	@ApiStatus.Internal
	public static <T> BiFunction<Value<?>, String, ? extends ClickableWidget> getWidgetFactory(Value<T> value) {
		// We are using a switch instead of just adding to our map for 2 reasons:
		// 1. It's (usually) faster than a map lookup, as most of the time the value will be one of these types
		// 2. It lets us handle the lowercased names of primitive types, which are different Class<> instances because reasons
		return switch (value.defaultValue()) {
			default -> widgetFactories.get(value.defaultValue().getClass());
		};
	}
}
