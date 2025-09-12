package dev.spiritstudios.specter.api.config.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.NestedConfigValue;
import dev.spiritstudios.specter.impl.config.client.NestedConfigScreen;
import dev.spiritstudios.specter.impl.config.client.gui.widget.OptionsScrollableWidget;
import dev.spiritstudios.specter.impl.core.Specter;

public abstract class ConfigScreen extends Screen {
	private static final Text MULTIPLAYER_SYNC_ERROR = Text.translatable("screen.specter.config.multiplayer_sync_error");

	protected final Config<?> config;
	protected final Screen parent;
	protected final String id;

	public ConfigScreen(Config<?> config, String id, Screen parent) {
		super(Text.translatable("config.%s.title".formatted(id)));
		this.config = config;
		this.parent = parent;
		this.id = id;
	}


	@Override
	protected void init() {
		super.init();
		Objects.requireNonNull(this.client);

		OptionsScrollableWidget scrollableWidget = new OptionsScrollableWidget(this.client, this.width, this.height - 64, 32, 25);

		List<ReflectionHelper.FieldValuePair<Value<?>>> values = config.fields();

		if (this.client.player != null && !this.client.isInSingleplayer()) {
			for (ReflectionHelper.FieldValuePair<Value<?>> pair : values) {
				if (!pair.value().sync()) continue;

				this.client.player.sendMessage(MULTIPLAYER_SYNC_ERROR, false);
				this.client.setScreen(this.parent);

				return;
			}
		}

		List<ClickableWidget> options = new ArrayList<>();

		values.forEach(pair -> {
			if (pair.value() instanceof NestedConfigValue<?> nestedOption) {
				String nestedId = "%s.%s".formatted(id, pair.value().name());
				ConfigScreen screen = new NestedConfigScreen(nestedOption.get(), nestedId, this);

				options.add(
						ButtonWidget.builder(
								Text.translatable("config.%s.title".formatted(nestedId)),
								button -> {
									save();
									this.client.setScreen(screen);
								}
						).dimensions(this.width / 2 - 100, 0, 200, 20).build()
				);

				return;
			}

			BiFunction<Value<?>, String, ? extends ClickableWidget> factory = ConfigScreenWidgets.getWidgetFactory(pair.value());
			if (factory == null) {
				Specter.LOGGER.warn("No widget factory found for {}", pair.value().defaultValue().getClass().getSimpleName());
				return;
			}

			ClickableWidget widget = factory.apply(pair.value(), id);
			if (widget == null)
				throw new IllegalStateException("Widget factory returned null for %s".formatted(pair.value().defaultValue().getClass().getSimpleName()));

			widget.setWidth(0);
			widget.setHeight(20);

			Text tooltip = Text.translatableWithFallback("%s.tooltip".formatted(pair.value().translationKey(id)), "");
			if (!tooltip.getString().isEmpty()) widget.setTooltip(Tooltip.of(tooltip));

			options.add(widget);
		});

		scrollableWidget.addOptions(options);
		this.addDrawableChild(scrollableWidget);
		this.addDrawableChild(new ButtonWidget.Builder(ScreenTexts.DONE, button -> close()).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
	}

	@Override
	public void close() {
		save();

		Objects.requireNonNull(this.client);
		this.client.setScreen(this.parent);
	}

	public abstract void save();

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
	}
}
