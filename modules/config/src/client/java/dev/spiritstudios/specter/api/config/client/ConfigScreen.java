package dev.spiritstudios.specter.api.config.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.NestedConfigValue;
import dev.spiritstudios.specter.impl.config.client.NestedConfigScreen;
import dev.spiritstudios.specter.impl.config.client.gui.widget.OptionsScrollableWidget;
import dev.spiritstudios.specter.impl.core.Specter;

public abstract class ConfigScreen extends Screen {
	private static final Component MULTIPLAYER_SYNC_ERROR = Component.translatable("screen.specter.config.multiplayer_sync_error");

	protected final Config<?> config;
	protected final Screen parent;
	protected final String id;

	public ConfigScreen(Config<?> config, String id, Screen parent) {
		super(Component.translatable("config.%s.title".formatted(id)));
		this.config = config;
		this.parent = parent;
		this.id = id;
	}


	@Override
	protected void init() {
		super.init();
		Objects.requireNonNull(this.minecraft);

		OptionsScrollableWidget scrollableWidget = new OptionsScrollableWidget(this.minecraft, this.width, this.height - 64, 32, 25);

		List<ReflectionHelper.FieldValuePair<Value<?>>> values = config.fields();

		if (this.minecraft.player != null && !this.minecraft.isLocalServer()) {
			for (ReflectionHelper.FieldValuePair<Value<?>> pair : values) {
				if (!pair.value().sync()) continue;

				this.minecraft.player.displayClientMessage(MULTIPLAYER_SYNC_ERROR, false);
				this.minecraft.setScreen(this.parent);

				return;
			}
		}

		List<AbstractWidget> options = new ArrayList<>();

		values.forEach(pair -> {
			if (pair.value() instanceof NestedConfigValue<?> nestedOption) {
				String nestedId = "%s.%s".formatted(id, pair.value().name());
				ConfigScreen screen = new NestedConfigScreen(nestedOption.get(), nestedId, this);

				options.add(
						Button.builder(
								Component.translatable("config.%s.title".formatted(nestedId)),
								button -> {
									save();
									minecraft.setScreen(screen);
								}
						).bounds(this.width / 2 - 100, 0, 200, 20).build()
				);

				return;
			}

			BiFunction<Value<?>, String, ? extends AbstractWidget> factory = ConfigScreenWidgets.getWidgetFactory(pair.value());
			if (factory == null) {
				Specter.LOGGER.warn("No widget factory found for {}", pair.value().defaultValue().getClass().getSimpleName());
				return;
			}

			AbstractWidget widget = factory.apply(pair.value(), id);
			if (widget == null)
				throw new IllegalStateException("Widget factory returned null for %s".formatted(pair.value().defaultValue().getClass().getSimpleName()));

			widget.setWidth(0);
			widget.setHeight(20);

			Component tooltip = Component.translatableWithFallback("%s.tooltip".formatted(pair.value().translationKey(id)), "");
			if (!tooltip.getString().isEmpty()) widget.setTooltip(Tooltip.create(tooltip));

			options.add(widget);
		});

		scrollableWidget.addOptions(options);
		this.addRenderableWidget(scrollableWidget);
		this.addRenderableWidget(new Button.Builder(CommonComponents.GUI_DONE, button -> onClose()).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
	}

	@Override
	public void onClose() {
		save();

		Objects.requireNonNull(this.minecraft);
		this.minecraft.setScreen(this.parent);
	}

	public abstract void save();

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
	}
}
