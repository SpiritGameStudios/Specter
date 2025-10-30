package dev.spiritstudios.specter.api.gui.client.widget;

import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SpecterButtonWidget extends Button {
	protected final Supplier<Component> message;

	protected SpecterButtonWidget(int x, int y, int width, int height, Supplier<Component> message, OnPress onPress, CreateNarration narrationSupplier) {
		super(x, y, width, height, message.get(), onPress, narrationSupplier);

		this.message = message;
	}

	public static dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget.Builder builder(Supplier<Component> message, OnPress onPress) {
		return new dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget.Builder(message, onPress);
	}

	@Override
	public Component getMessage() {
		return message.get();
	}

	public static final class Builder {
		private final Supplier<Component> message;
		private final Button.OnPress onPress;
		@Nullable
		private Tooltip tooltip;
		private int x;
		private int y;
		private int width = 150;
		private int height = 20;
		private Button.CreateNarration narrationSupplier = Button.DEFAULT_NARRATION;

		public Builder(Supplier<Component> message, Button.OnPress onPress) {
			this.message = message;
			this.onPress = onPress;
		}

		public dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget.Builder position(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget.Builder width(int width) {
			this.width = width;
			return this;
		}

		public dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget.Builder height(int height) {
			this.height = height;
			return this;
		}

		public dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget.Builder size(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget.Builder dimensions(int x, int y, int width, int height) {
			return this.position(x, y).size(width, height);
		}

		public dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget.Builder tooltip(@Nullable Tooltip tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public dev.spiritstudios.specter.api.gui.client.widget.SpecterButtonWidget.Builder narrationSupplier(Button.CreateNarration narrationSupplier) {
			this.narrationSupplier = narrationSupplier;
			return this;
		}

		public SpecterButtonWidget build() {
			SpecterButtonWidget buttonWidget = new SpecterButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
			buttonWidget.setTooltip(this.tooltip);
			return buttonWidget;
		}
	}
}
