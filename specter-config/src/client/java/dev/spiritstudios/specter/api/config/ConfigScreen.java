package dev.spiritstudios.specter.api.config;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.config.NestedConfigValue;
import dev.spiritstudios.specter.impl.config.gui.widget.OptionsScrollableWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class ConfigScreen extends Screen {
	private static final Text MULTIPLAYER_SYNC_ERROR = Text.translatable("screen.specter.config.multiplayer_sync_error");

	private final Config<?> config;
	private final Screen parent;
	private final String id;
	private final ConfigHolder<?, ?> holder;

	public ConfigScreen(Config<?> config, String id, Screen parent) {
		this(config, id, parent, null);
	}

	public ConfigScreen(Config<?> config, String id, Screen parent, ConfigHolder<?, ?> holder) {
		super(Text.translatable("config.%s.title".formatted(id)));
		this.config = config;
		this.parent = parent;
		this.id = id;
		this.holder = holder;
	}


	@Override
	protected void init() {
		super.init();
		Objects.requireNonNull(this.client);

		OptionsScrollableWidget scrollableWidget = new OptionsScrollableWidget(this.client, this.width, this.height - 64, 32, 25);

		List<Value<?>> values = config.getValues().toList();

		if (this.client.player != null && !this.client.isInSingleplayer()) {
			for (Value<?> option : values) {
				if (!option.sync()) continue;

				this.client.player.sendMessage(MULTIPLAYER_SYNC_ERROR, false);
				this.client.setScreen(this.parent);

				return;
			}
		}

		List<ClickableWidget> options = new ArrayList<>();

		values.forEach(option -> {
			if (option instanceof NestedConfigValue<?> nestedOption) {
				String nestedId = "%s.%s".formatted(id, option.name());
				ConfigScreen screen = new ConfigScreen(nestedOption.get(), nestedId, this);

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

			BiFunction<Value<?>, String, ? extends ClickableWidget> factory = ConfigScreenWidgets.getWidgetFactory(option);
			if (factory == null) {
				SpecterGlobals.LOGGER.warn("No widget factory found for {}", option.defaultValue().getClass().getSimpleName());
				return;
			}

			ClickableWidget widget = factory.apply(option, id);
			if (widget == null)
				throw new IllegalStateException("Widget factory returned null for %s".formatted(option.defaultValue().getClass().getSimpleName()));

			widget.setWidth(0);
			widget.setHeight(20);

			Text tooltip = Text.translatableWithFallback("%s.tooltip".formatted(option.translationKey(id)), "");
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

	public void save() {
		if (this.parent instanceof ConfigScreen screen) screen.save();

		if (this.holder == null) return;
		holder.save();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);

		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
	}
}