package dev.spiritstudios.specter.impl.config.client.gui.widget.gamerule;


import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.config.client.TabbedListConfigScreen;

public class BooleanValueWidget extends TabbedListConfigScreen.ValueWidget {
	public static final TabbedListConfigScreen.ValueWidgetFactory<Boolean> FACTORY = new TabbedListConfigScreen.ValueWidgetFactory<>() {
		@Override
		public TabbedListConfigScreen.ValueWidget create(MinecraftClient client, String translationPrefix, List<OrderedText> description, Text narration, Text name, Value<Boolean> value) {
			return new BooleanValueWidget(client, description, narration, name, value);
		}

		@Override
		public Text toString(String translationPrefix, Boolean value) {
			return ScreenTexts.onOrOff(value);
		}
	};


	private final CyclingButtonWidget<Boolean> button;
	private final Value<Boolean> value;

	public BooleanValueWidget(
			MinecraftClient client,
			@Nullable List<OrderedText> description,
			Text narration,
			Text name,
			Value<Boolean> value
	) {
		super(client, description, name);

		this.value = value;

		this.button = CyclingButtonWidget.onOffBuilder(value.get())
				.omitKeyText()
				.narration(button ->
						button.getGenericNarrationMessage().append("\n").append(narration))
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
