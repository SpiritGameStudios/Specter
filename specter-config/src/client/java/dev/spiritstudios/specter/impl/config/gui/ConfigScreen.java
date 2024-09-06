package dev.spiritstudios.specter.impl.config.gui;

import dev.spiritstudios.specter.api.ConfigScreenManager;
import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.gui.widget.OptionsScrollableWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class ConfigScreen extends Screen {
	private final Config<?> config;
	private final Screen parent;

	public ConfigScreen(Config<?> config, Screen parent) {
		super(Text.translatable("config.%s.%s.title".formatted(config.getId().getNamespace(), config.getId().getPath())));
		this.config = config;
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		if (this.client == null) close();

		OptionsScrollableWidget scrollableWidget = new OptionsScrollableWidget(this.client, this.width, this.height - 64, 32, 25);
		List<ClickableWidget> options = new ArrayList<>();
		List<? extends Config.Value<?>> values = Arrays.stream(config.getClass().getDeclaredFields())
			.filter(field -> field.getType().isAssignableFrom(Config.Value.class))
			.map(field -> (Config.Value<?>) ReflectionHelper.getFieldValue(config, field))
			.filter(Objects::nonNull)
			.toList();


		boolean failed = false;
		for (Config.Value<?> option : values) {
			if (!option.sync() || this.client.player == null || this.client.isInSingleplayer()) continue;

			this.client.player.sendMessage(Text.of("Configs cannot be edited in multiplayer"), true);
			this.client.setScreen(this.parent);
			failed = true;
			break;
		}
		if (failed) return;

		values.forEach(option -> {
			BiFunction<Config.Value<?>, Identifier, ? extends ClickableWidget> factory = ConfigScreenManager.getWidgetFactory(option, this.config.getId());
			if (factory == null) {
				SpecterGlobals.LOGGER.warn("No widget factory found for {}", option.defaultValue().getClass().getSimpleName());
				return;
			}

			ClickableWidget widget = factory.apply(option, this.config.getId());
			if (widget != null) options.add(widget);
		});

		scrollableWidget.addOptions(Arrays.copyOf(options.toArray(), options.size(), ClickableWidget[].class));
		this.addDrawableChild(scrollableWidget);
		this.addDrawableChild(new ButtonWidget.Builder(Text.translatable("gui.done"), button -> close()).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
	}

	@Override
	public void close() {
		save();

		if (this.client == null) return;
		this.client.setScreen(this.parent);
	}

	public void save() {
		config.save();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);

		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
	}
}
