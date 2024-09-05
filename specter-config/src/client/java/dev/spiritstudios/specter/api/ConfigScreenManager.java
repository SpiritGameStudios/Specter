package dev.spiritstudios.specter.api;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.impl.config.gui.widget.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.BiFunction;

public final class ConfigScreenManager {
	private static final Map<Class<?>, BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget>> widgetFactories = new Object2ObjectOpenHashMap<>();

	public static void registerWidgetFactory(Class<?> clazz, BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> factory) {
		widgetFactories.put(clazz, factory);
	}

	@SuppressWarnings("unchecked")
	@ApiStatus.Internal
	public static <T> BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> getWidgetFactory(Config.Value<T> value, Identifier configId) {
		// We are using a switch instead of just adding to our map for 2 reasons:
		// 1. It's (usually) faster than a map lookup, as most of the time the value will be one of these types
		// 2. It lets us handle the lowercased names of primitive types, which are different Class<> instances because reasons
		return switch (value.defaultValue()) {
			case Boolean ignored ->
				(configValue, id) -> new BooleanButtonWidget((Config.Value<Boolean>) configValue, id);
			case Integer ignored ->
				(configValue, id) -> new IntegerSliderWidget((Config.Value<Integer>) configValue, id);
			case Double ignored -> (configValue, id) -> new DoubleSliderWidget((Config.Value<Double>) configValue, id);
			case Float ignored -> (configValue, id) -> new FloatSliderWidget((Config.Value<Float>) configValue, id);
			case String ignored -> (configValue, id) -> new TextBoxWidget((Config.Value<String>) configValue, id);
			case Enum<?> ignored -> (configValue, id) -> new EnumButtonWidget((Config.Value<Enum<?>>) configValue, id);
			default -> widgetFactories.get(value.defaultValue().getClass());
		};
	}

	private ConfigScreenManager() {
	}
}
