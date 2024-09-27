package dev.spiritstudios.specter.api.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.spiritstudios.specter.api.core.util.Range;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Function;

public class SpecterSliderWidget extends ClickableWidget {
	private static final Identifier SLIDER = Identifier.ofVanilla("widget/slider");
	private static final Identifier SLIDER_HIGHLIGHTED = Identifier.ofVanilla("widget/slider_highlighted");
	private static final Identifier SLIDER_HANDLE = Identifier.ofVanilla("widget/slider_handle");
	private static final Identifier SLIDER_HANDLE_HIGHLIGHTED = Identifier.ofVanilla("widget/slider_handle_highlighted");

	private static final Range<Double> ZERO_ONE = new Range<>(0.0, 1.0);

	protected double value;
	protected final double step;
	protected final Range<Double> range;
	protected final Consumer<Double> valueChangedListener;
	protected final Function<Double, Text> messageSupplier;

	protected boolean sliderFocused;

	protected SpecterSliderWidget(int x, int y, int width, int height, double value, double step, Range<Double> range, Consumer<Double> valueChangedListener, Function<Double, Text> messageSupplier) {
		super(x, y, width, height, messageSupplier.apply(value));

		this.value = value;
		this.step = step;
		this.range = range;
		this.valueChangedListener = valueChangedListener;
		this.messageSupplier = messageSupplier;
	}

	public static Builder builder(double value) {
		return new Builder(value);
	}

	// region Input
	@Override
	public void onClick(double mouseX, double mouseY) {
		this.setValueFromMouse(mouseX);
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		this.setValueFromMouse(mouseX);
		super.onDrag(mouseX, mouseY, deltaX, deltaY);
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		super.playDownSound(MinecraftClient.getInstance().getSoundManager());
	}

	private void setValueFromMouse(double mouseX) {
		setValue((mouseX - (double) (this.getX() + 4)) / (double) (this.getWidth() - 8));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (KeyCodes.isToggle(keyCode)) {
			this.sliderFocused = !this.sliderFocused;
			return true;
		}

		if (!this.sliderFocused) return false;

		if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT) {
			float sign = keyCode == GLFW.GLFW_KEY_LEFT ? -1.0F : 1.0F;
			this.setValue(this.value + (sign / (float) (this.width - 8)));

			return true;
		}

		return false;
	}
	// endregion

	// region Navigation
	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			this.sliderFocused = false;
			return;
		}

		GuiNavigationType navigationType = MinecraftClient.getInstance().getNavigationType();
		if (navigationType == GuiNavigationType.MOUSE || navigationType == GuiNavigationType.KEYBOARD_TAB)
			this.sliderFocused = true;
	}

	protected void onValueChanged() {
		this.valueChangedListener.accept(getValue());
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}
	// endregion

	// region Rendering
	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		MinecraftClient client = MinecraftClient.getInstance();
		context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		context.drawGuiTexture(
			this.getTexture(),
			this.getX(),
			this.getY(),
			this.getWidth(),
			this.getHeight()
		);

		context.drawGuiTexture(
			this.getHandleTexture(),
			this.getX() + (int) (this.value * (this.getWidth() - 8)),
			this.getY(),
			8,
			this.getHeight()
		);

		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int color = this.active ? 0xffffff : 0xa0a0a0;

		this.drawScrollableText(context, client.textRenderer, 2, color | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}

	@Override
	public Text getMessage() {
		return this.messageSupplier.apply(getValue());
	}

	protected Identifier getTexture() {
		return this.isFocused() && !this.sliderFocused ? SLIDER_HIGHLIGHTED : SLIDER;
	}

	protected Identifier getHandleTexture() {
		return !this.hovered && !this.sliderFocused ? SLIDER_HANDLE : SLIDER_HANDLE_HIGHLIGHTED;
	}
	// endregion

	// region Narration
	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getNarrationMessage());
		if (!this.active) return;

		builder.put(
			NarrationPart.USAGE,
			Text.translatable(isFocused() ? "narration.slider.usage.focused" : "narration.slider.usage.hovered")
		);
	}

	@Override
	protected MutableText getNarrationMessage() {
		return Text.translatable("gui.narrate.slider", this.getMessage());
	}
	// endregion

	protected void setValue(double value) {
		double oldValue = this.value;

		double newValue = MathHelper.clamp(value, 0.0, 1.0);
		newValue = step <= 0.0 ? newValue : step * Math.round(newValue / step);
		
		this.value = MathHelper.clamp(newValue, 0.0, 1.0);

		if (oldValue != this.value) onValueChanged();
	}

	public double getValue() {
		return this.range.map(this.value, ZERO_ONE);
	}

	public static class Builder {
		private int x;
		private int y;

		private int width = 150;
		private int height = 20;

		private double step;
		private Range<Double> range = new Range<>(0.0, 1.0);
		private Consumer<Double> valueChangedListener = value -> {
		};
		private Function<Double, Text> messageSupplier = (value) -> Text.of(String.format("%.2f", value));

		private final double value;

		protected Builder(double value) {
			this.value = value;
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

		public Builder dimensions(int width, int height, int x, int y) {
			return position(x, y).size(width, height);
		}

		public Builder message(Text message) {
			messageSupplier = (ignored) -> message;
			return this;
		}

		public Builder message(Function<Double, Text> messageSupplier) {
			this.messageSupplier = messageSupplier;
			return this;
		}

		public Builder step(double step) {
			this.step = step;
			return this;
		}

		public Builder range(Range<Double> range) {
			this.range = range;
			return this;
		}

		public Builder range(double min, double max) {
			return range(new Range<>(min, max));
		}

		public Builder onValueChanged(Consumer<Double> valueChangedListener) {
			this.valueChangedListener = valueChangedListener;
			return this;
		}

		public SpecterSliderWidget build() {
			return new SpecterSliderWidget(
				x,
				y,
				width,
				height,
				value,
				step,
				range,
				valueChangedListener,
				messageSupplier
			);
		}
	}
}