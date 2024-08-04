package dev.spiritstudios.specter.impl.config.gui;

import dev.spiritstudios.specter.api.base.util.ReflectionHelper;
import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.NestedConfig;
import dev.spiritstudios.specter.api.config.annotations.Range;
import dev.spiritstudios.specter.impl.base.Specter;
import dev.spiritstudios.specter.impl.config.gui.widget.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigScreen extends Screen {
	private final Config config;
	private final Screen parent;
	private OptionsScrollableWidget scrollableWidget;

	public ConfigScreen(Config config, Screen parent) {
		super(Text.translatable("config." + config.getId() + ".title"));
		this.config = config;
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();

		this.scrollableWidget = new OptionsScrollableWidget(this.client, this.width, this.height - 64, 32, 25);
		List<ClickableWidget> options = new ArrayList<>();
		for (Field field : config.getClass().getDeclaredFields()) addOptionWidget(field, options);

		this.scrollableWidget.addOptions(Arrays.copyOf(options.toArray(), options.size(), ClickableWidget[].class));
		this.addDrawableChild(this.scrollableWidget);
		this.addDrawableChild(new ButtonWidget.Builder(Text.translatable("gui.done"), button -> close()).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
	}

	@Override
	public void close() {
		save();

		if (this.client == null) return;
		this.client.setScreen(this.parent);
	}

	public void save() {
		if (config instanceof NestedConfig && this.parent instanceof ConfigScreen) {
			((ConfigScreen) this.parent).save();
			return;
		}
		config.save();
	}

	private void addOptionWidget(Field option, List<ClickableWidget> options) {
		Object value = ReflectionHelper.getFieldValue(config, option);
		options.add(switch (value) {
			case String ignored ->
				new TextBoxWidget(getTranslationKey(option), () -> (String) value, newValue -> ReflectionHelper.setFieldValue(config, option, newValue));

			case Boolean ignored ->
				new BooleanButtonWidget(getTranslationKey(option), () -> (Boolean) value, newValue -> ReflectionHelper.setFieldValue(config, option, newValue));

			case Float ignored -> {
				float min = 0;
				float max = 100;
				if (option.isAnnotationPresent(Range.class)) {
					Range range = option.getAnnotation(Range.class);
					min = (float) range.min();
					max = (float) range.max();
				}

				yield new FloatSliderWidget(
					getTranslationKey(option),
					min,
					max,
					() -> ReflectionHelper.getFieldValue(config, option),
					newValue -> ReflectionHelper.setFieldValue(config, option, newValue)
				);
			}

			case Integer ignored -> {
				int min = 0;
				int max = 100;
				if (option.isAnnotationPresent(Range.class)) {
					Range range = option.getAnnotation(Range.class);
					min = (int) range.min();
					max = (int) range.max();
				}

				yield new IntegerSliderWidget(
					getTranslationKey(option),
					min,
					max,
					() -> ReflectionHelper.getFieldValue(config, option),
					newValue -> ReflectionHelper.setFieldValue(config, option, newValue)
				);
			}

			case Double ignored -> {
				double min = 0;
				double max = 100;
				if (option.isAnnotationPresent(Range.class)) {
					Range range = option.getAnnotation(Range.class);
					min = range.min();
					max = range.max();
				}

				yield new DoubleSliderWidget(
					getTranslationKey(option),
					min,
					max,
					() -> ReflectionHelper.getFieldValue(config, option),
					newValue -> ReflectionHelper.setFieldValue(config, option, newValue)
				);
			}

			case Enum<?> ignored ->
				new EnumButtonWidget(option.getName(), () -> (Enum<?>) value, newValue -> ReflectionHelper.setFieldValue(config, option, newValue), (Enum<?>) value);

			case NestedConfig nestedValue -> {
				getNestedClass(options, nestedValue);
				yield null;
			}

			case null, default -> {
				if (Specter.DEBUG)
					Specter.LOGGER.warn("Unsupported config type: {}", option.getType().getName());

				yield null;
			}
		});
	}

	private void getNestedClass(List<ClickableWidget> options, NestedConfig value) {
		ConfigScreen nestedScreen = new ConfigScreen(value, this);

		for (Field nestedField : value.getClass().getDeclaredFields()) {
			if (nestedField.getType().isAssignableFrom(NestedConfig.class)) {
				NestedConfig nestedValue = ReflectionHelper.getFieldValue(value.getClass().getDeclaredFields(), nestedField);
				getNestedClass(options, nestedValue);
			}
		}

		options.add(new ButtonWidget.Builder(
			Text.translatable("config." + value.getId() + ".title"),
			button -> {
				save();

				if (this.client == null) return;
				this.client.setScreen(nestedScreen);
			}
		).dimensions(this.width / 2 - 100, 0, 200, 20).build());
	}

	private String getTranslationKey(Field field) {
		return "config." + this.config.getId() + "." + field.getName();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);

		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
	}
}
