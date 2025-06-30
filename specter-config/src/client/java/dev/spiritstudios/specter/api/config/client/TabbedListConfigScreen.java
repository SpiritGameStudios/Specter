package dev.spiritstudios.specter.api.config.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.client.gui.widget.gamerule.BooleanValueWidget;
import dev.spiritstudios.specter.impl.config.client.gui.widget.gamerule.EnumValueWidget;
import dev.spiritstudios.specter.impl.config.client.gui.widget.gamerule.GameruleConfigWidgetFactories;

/**
 * A simple config screen, meant to look very similar to the game rule editing screen.
 */
public class TabbedListConfigScreen extends Screen {
	protected final ConfigHolder<?, ?> holder;
	protected final Screen parent;

	protected final String translationPrefix;

	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private ValueListWidget list;
	private ButtonWidget doneButton;

	public TabbedListConfigScreen(ConfigHolder<?, ?> holder, Screen parent) {
		super(Text.translatable("config.%s.title".formatted(holder.id().toTranslationKey())));

		this.translationPrefix = "config." + holder.id().toTranslationKey() + ".";
		this.holder = holder;
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.layout.addHeader(translatable("title"), this.textRenderer);
		this.list = this.layout.addBody(new ValueListWidget(
				width,
				layout.getContentHeight(),
				layout.getHeaderHeight(),
				holder
		));

		DirectionalLayoutWidget buttons = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));

		this.doneButton = buttons.add(ButtonWidget.builder(
				ScreenTexts.DONE,
				button -> {
					for (ValueWidget value : this.list.valueWidgets) value.apply();
					holder.save();
					this.close();
				}
		).build());

		buttons.add(ButtonWidget.builder(
				ScreenTexts.CANCEL,
				button -> this.close()
		).build());

		this.layout.forEachChild(this::addDrawableChild);
		this.refreshWidgetPositions();
	}

	@Override
	protected void refreshWidgetPositions() {
		this.layout.refreshPositions();
		if (this.list != null) {
			this.list.position(this.width, this.layout);
		}
	}

	@Override
	public void close() {
		this.client.setScreen(parent);
	}

	private Text translatable(String key) {
		return Text.translatable(translationPrefix + key);
	}

	@FunctionalInterface
	public interface ValueWidgetFactory<T> {
		TabbedListConfigScreen.ValueWidget create(MinecraftClient client,
												  String translationPrefix,
												  List<OrderedText> description,
												  Text narration,
												  Text name,
												  Value<T> value);

		default Text toString(String translationPrefix, T value) {
			return Text.literal(String.valueOf(value));
		}
	}

	public abstract static class ValueWidget extends ElementListWidget.Entry<ValueWidget> {
		protected final @Nullable List<OrderedText> description;
		protected final List<OrderedText> name;
		protected final List<ClickableWidget> children = new ArrayList<>();
		protected final MinecraftClient client;

		public ValueWidget(MinecraftClient client, @Nullable List<OrderedText> description, Text name) {
			this.client = client;
			this.description = description;
			this.name = client.textRenderer.wrapLines(name, 175);
		}

		public abstract void apply();

		@Override
		public List<? extends Element> children() {
			return this.children;
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.children;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
			if (this.name.size() == 1) {
				context.drawTextWithShadow(client.textRenderer, this.name.getFirst(), x, y + 5, -1);
			} else if (this.name.size() >= 2) {
				context.drawTextWithShadow(client.textRenderer, this.name.get(0), x, y, -1);
				context.drawTextWithShadow(client.textRenderer, this.name.get(1), x, y + 10, -1);
			}
		}
	}

	private class ValueListWidget extends ElementListWidget<ValueWidget> {
		private final List<ValueWidget> valueWidgets;

		public ValueListWidget(int width, int height, int y, ConfigHolder<?, ?> holder) {
			super(TabbedListConfigScreen.this.client, width, height, y, 24);

			ImmutableList.Builder<ValueWidget> builder = ImmutableList.builder();

			holder.get().values().forEach((key, either) -> {
				ValueWidget widget = either.map(
						value -> {
							ValueWidgetFactory<?> factory = switch (value.defaultValue()) {
								case Boolean ignored -> BooleanValueWidget.FACTORY;
								case Integer ignored -> GameruleConfigWidgetFactories.INTEGER;
								case Float ignored -> GameruleConfigWidgetFactories.FLOAT;
								case Double ignored -> GameruleConfigWidgetFactories.DOUBLE;
								case Long ignored -> GameruleConfigWidgetFactories.LONG;
								case Enum<?> ignored -> EnumValueWidget.FACTORY;
								default -> null;
							};
							if (factory == null) return null;

							return createValueWidget(
									holder,
									key,
									value,
									textRenderer,
									factory
							);
						},
						subConfig -> {
							return null;
						});

				if (widget != null) {
					addEntry(widget);
					builder.add(widget);
				}
			});

			this.valueWidgets = builder.build();
		}

		@Override
		public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
			super.renderWidget(context, mouseX, mouseY, deltaTicks);
			ValueWidget hovered = this.getHoveredEntry();
			if (hovered != null && hovered.description != null) {
				TabbedListConfigScreen.this.setTooltip(hovered.description);
			}
		}

		private <T> ValueWidget createValueWidget(
				ConfigHolder<?, ?> holder,
				String key,
				Value<?> value,
				TextRenderer textRenderer,
				ValueWidgetFactory<T> factory
		) {
			Optional<Value<T>> casted = ReflectionHelper.cast(value);
			if (casted.isEmpty())
				throw new IllegalArgumentException("Passed a value of a different type to its factory to createValueWidget.");

			String translationPrefix = "config." + holder.id().toTranslationKey() + "." + key;

			Text name = Text.translatable(translationPrefix);
			Text serializedName = Text.literal(key).formatted(Formatting.YELLOW);

			Text defaultValue = Text.translatable(
					"editGamerule.default",
					factory.toString(translationPrefix, casted.get().defaultValue())
			).formatted(Formatting.GRAY);

			String descriptionKey = translationPrefix + ".description";

			List<OrderedText> description;
			Text narration;

			if (I18n.hasTranslation(descriptionKey)) {
				ImmutableList.Builder<OrderedText> builder = ImmutableList.builder();

				builder.add(serializedName.asOrderedText());

				MutableText descriptionText = Text.translatable(descriptionKey);
				textRenderer.wrapLines(descriptionText, 150).forEach(builder::add);

				builder.add(defaultValue.asOrderedText());

				description = builder.build();
				narration = descriptionText.append("\n").append(defaultValue.getString());
			} else {
				description = ImmutableList.of(serializedName.asOrderedText(), defaultValue.asOrderedText());
				narration = defaultValue;
			}

			return factory.create(
					client,
					translationPrefix,
					description,
					narration,
					name,
					casted.get()
			);
		}
	}
}
