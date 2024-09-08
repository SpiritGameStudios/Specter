package dev.spiritstudios.specter.api;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.impl.config.gui.widget.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.BiFunction;

@SuppressWarnings("unchecked")
public final class ConfigScreenManager {
	private static final Map<Class<?>, BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget>> widgetFactories = new Object2ObjectOpenHashMap<>();

	public static void registerWidgetFactory(Class<?> clazz, BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> factory) {
		widgetFactories.put(clazz, factory);
	}

	@ApiStatus.Internal
	public static <T> BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> getWidgetFactory(Config.Value<T> value) {
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

	private static final BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> BOOLEAN_WIDGET_FACTORY = (configValue, id) -> new BooleanButtonWidget((Config.Value<Boolean>) configValue, id);
	private static final BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> INTEGER_WIDGET_FACTORY = (configValue, id) -> new IntegerSliderWidget((Config.Value<Integer>) configValue, id);
	private static final BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> DOUBLE_WIDGET_FACTORY = (configValue, id) -> new DoubleSliderWidget((Config.Value<Double>) configValue, id);
	private static final BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> FLOAT_WIDGET_FACTORY = (configValue, id) -> new FloatSliderWidget((Config.Value<Float>) configValue, id);
	private static final BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> STRING_WIDGET_FACTORY = (configValue, id) -> new TextBoxWidget((Config.Value<String>) configValue, id);
	private static final BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> ENUM_WIDGET_FACTORY = (configValue, id) -> new EnumButtonWidget((Config.Value<Enum<?>>) configValue, id);

	private ConfigScreenManager() {
	}
}
