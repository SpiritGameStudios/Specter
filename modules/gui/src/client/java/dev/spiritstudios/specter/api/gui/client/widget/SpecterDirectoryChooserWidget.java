package dev.spiritstudios.specter.api.gui.client.widget;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SpecterDirectoryChooserWidget extends AbstractWidget {
	protected final Function<Optional<File>, Component> messageSupplier;
	protected final Consumer<Optional<File>> valueChangedListener;
	protected final DirectoryChooserType type;
	@Nullable
	protected File value;

	public SpecterDirectoryChooserWidget(int x, int y, int width, int height, @Nullable File value, Function<Optional<File>, Component> messageSupplier, Consumer<Optional<File>> valueChangedListener, DirectoryChooserType type) {
		super(x, y, width, height, messageSupplier.apply(null));

		this.messageSupplier = messageSupplier;
		this.valueChangedListener = valueChangedListener;
		this.type = type;
		this.value = value;
	}

	public static Builder builder(@Nullable File value, DirectoryChooserType type) {
		return new Builder(value, type);
	}

	@Override
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {

	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {

	}

	protected void setValue(@Nullable File value) {
		this.value = value;
	}

	public enum DirectoryChooserType {
		FILE,
		FOLDER
	}

	public static class Builder {
		@Nullable
		private final File value;
		private final DirectoryChooserType type;
		private int x;
		private int y;
		private int width = 150;
		private int height = 20;
		private Function<Optional<File>, Component> messageSupplier = (value) -> Component.nullToEmpty("%s".formatted(value));
		private Consumer<Optional<File>> valueChangedListener = value -> {
		};

		protected Builder(@Nullable File value, DirectoryChooserType type) {
			this.value = value;
			this.type = type;
		}

		public Builder position(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder size(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public Builder dimensions(int x, int y, int width, int height) {
			return position(x, y).size(width, height);
		}

		public Builder message(Function<Optional<File>, Component> messageSupplier) {
			this.messageSupplier = messageSupplier;
			return this;
		}

		public Builder onValueChanged(Consumer<Optional<File>> valueChangedListener) {
			this.valueChangedListener = valueChangedListener;
			return this;
		}

		public SpecterDirectoryChooserWidget build() {
			return new SpecterDirectoryChooserWidget(
					this.x,
					this.y,
					this.width,
					this.height,
					this.value,
					this.messageSupplier,
					this.valueChangedListener,
					this.type
			);
		}
	}
}
