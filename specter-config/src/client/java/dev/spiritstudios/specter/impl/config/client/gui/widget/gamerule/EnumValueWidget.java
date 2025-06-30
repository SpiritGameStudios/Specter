package dev.spiritstudios.specter.impl.config.client.gui.widget.gamerule;


import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.config.client.TabbedListConfigScreen;

public class EnumValueWidget<T extends Enum<T>> extends TabbedListConfigScreen.ValueWidget {
	public static final TabbedListConfigScreen.ValueWidgetFactory<Enum<?>> FACTORY = new TabbedListConfigScreen.ValueWidgetFactory<>() {
		@Override
		public TabbedListConfigScreen.ValueWidget create(MinecraftClient client, String translationPrefix, List<OrderedText> description, Text narration, Text name, Value<Enum<?>> value) {
			return new EnumValueWidget<>(client, translationPrefix, description, narration, name, value);
		}

		@Override
		public Text toString(String translationPrefix, Enum<?> value) {
			return Text.translatable("%s.%s".formatted(translationPrefix, value.toString().toLowerCase()));
		}
	};


	private final CyclingButtonWidget<Enum<T>> button;
	private final Value<Enum<T>> value;

	@SuppressWarnings("unchecked")
	public EnumValueWidget(
			MinecraftClient client,
			String translationPrefix,
			@Nullable List<OrderedText> description,
			Text narration,
			Text name,
			Value<Enum<?>> value
	) {
		super(client, description, name);

		this.value = (Value<Enum<T>>) (Object) value;

		this.button = CyclingButtonWidget.<Enum<T>>builder(val -> FACTORY.toString(translationPrefix, val))
				.narration(button ->
						button.getGenericNarrationMessage().append("\n").append(narration))
				.values(this.value.defaultValue().getClass().getEnumConstants())
				.initially(this.value.get())
				.omitKeyText()
				.build(
						10, 5,
						44, 20,
						name
				);

		this.children.add(this.button);
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);

		this.button.setX(x + entryWidth - 45);
		this.button.setY(y);
		this.button.render(context, mouseX, mouseY, tickProgress);
	}

	@Override
	public void apply() {
		this.value.set(button.getValue());
	}
}
