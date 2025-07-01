package dev.spiritstudios.specter.impl.config.client.gui.widget.gamerule;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.config.client.TabbedListConfigScreen;

public class StringInputWidget extends TabbedListConfigScreen.ValueWidget {
	private final TextFieldWidget textField;
	private final Value<String> value;

	private boolean valid = true;

	public StringInputWidget(
			MinecraftClient client,
			String translationPrefix,
			@Nullable List<OrderedText> description,
			Text narration,
			Text name,
			Value<String> value
	) {
		super(client, description, name);

		this.value = value;

		this.textField = new TextFieldWidget(
				client.textRenderer,
				10, 5,
				160, 20,
				name
		);

		this.textField.setText(value.get());
		this.textField.setChangedListener(s -> {
			if (value.checkConstraints(s).isSuccess()) {
				this.textField.setEditableColor(0xe0e0e0);
				this.valid = true;
			} else {
				this.textField.setEditableColor(0xff0000);
				this.valid = false;
			}
		});

		this.children.add(this.textField);
	}

	@Override
	public void apply() {
		if (valid) value.set(textField.getText());
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);

		this.textField.setX(x + entryWidth - 165);
		this.textField.setY(y);
		this.textField.render(context, mouseX, mouseY, tickProgress);
	}
}
