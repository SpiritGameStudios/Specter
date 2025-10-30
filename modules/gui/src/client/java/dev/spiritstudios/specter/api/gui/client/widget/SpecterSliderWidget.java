package dev.spiritstudios.specter.api.gui.client.widget;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class SpecterSliderWidget extends AbstractWidget {
	private static final ResourceLocation SLIDER = ResourceLocation.withDefaultNamespace("widget/slider");
	private static final ResourceLocation SLIDER_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("widget/slider_highlighted");
	private static final ResourceLocation SLIDER_HANDLE = ResourceLocation.withDefaultNamespace("widget/slider_handle");
	private static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("widget/slider_handle_highlighted");

	protected final double step;
	protected final double min;
	protected final double max;

	protected final DoubleConsumer valueChangedListener;
	protected final DoubleFunction<Component> messageSupplier;
	protected double value;
	protected boolean sliderFocused;

	protected SpecterSliderWidget(int x, int y, int width, int height, double value, double step, double min, double max, DoubleConsumer valueChangedListener, DoubleFunction<Component> messageSupplier) {
		super(x, y, width, height, messageSupplier.apply(value));

		this.value = value;
		this.step = step;
		this.min = min;
		this.max = max;
		this.valueChangedListener = valueChangedListener;
		this.messageSupplier = messageSupplier;
	}

	public static Builder builder(double value) {
		return new Builder(value);
	}

	// region Input


	@Override
	public void onClick(MouseButtonEvent click, boolean doubled) {
		super.onClick(click, doubled);

		if (click.isLeft()) {
			this.setValueFromMouse(click.x());
		}
	}

	@Override
	public void onRelease(MouseButtonEvent click) {
		super.onRelease(click);

		if (click.isLeft()) {
			super.playDownSound(Minecraft.getInstance().getSoundManager());
		}
	}

	@Override
	protected void onDrag(MouseButtonEvent click, double offsetX, double offsetY) {
		super.onDrag(click, offsetX, offsetY);
		this.setValueFromMouse(offsetX);
	}

	private void setValueFromMouse(double mouseX) {
		setValue(Mth.map(Mth.clamp((mouseX - (double) (this.getX() + 4)) / (double) (this.getWidth() - 8), 0.0, 1.0), 0, 1, min, max));
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (input.isSelection()) {
			this.sliderFocused = !this.sliderFocused;
			return true;
		}

		if (!this.sliderFocused) return false;

		if (input.isLeft() || input.isRight()) {
			float sign = input.isLeft() ? -1.0F : 1.0F;
			this.setValue(this.value + sign * (this.step == 0.0 ? 0.01 : this.step));

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

		InputType navigationType = Minecraft.getInstance().getLastInputType();
		if (navigationType == InputType.MOUSE || navigationType == InputType.KEYBOARD_TAB)
			this.sliderFocused = true;
	}

	protected void onValueChanged() {
		this.valueChangedListener.accept(value);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}
	// endregion

	// region Rendering
	@Override
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
		Minecraft client = Minecraft.getInstance();

		context.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				this.getTexture(),
				this.getX(),
				this.getY(),
				this.getWidth(),
				this.getHeight(),
				ARGB.colorFromFloat(this.alpha, 1.0F, 1.0F, 1.0F)
		);

		context.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				this.getHandleTexture(),
				this.getX() + (int) (Mth.map(this.value, min, max, 0, 1) * (this.getWidth() - 8)),
				this.getY(),
				8,
				this.getHeight(),
				ARGB.colorFromFloat(this.alpha, 1.0F, 1.0F, 1.0F)
		);

		int color = this.active ? 0xffffff : 0xa0a0a0;

		this.renderScrollingString(context, client.font, 2, color | Mth.ceil(this.alpha * 255.0F) << 24);
	}

	@Override
	public Component getMessage() {
		return this.messageSupplier.apply(value);
	}

	protected ResourceLocation getTexture() {
		return this.isFocused() && !this.sliderFocused ? SLIDER_HIGHLIGHTED : SLIDER;
	}

	protected ResourceLocation getHandleTexture() {
		return !this.isHovered && !this.sliderFocused ? SLIDER_HANDLE : SLIDER_HANDLE_HIGHLIGHTED;
	}
	// endregion

	// region Narration
	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
		builder.add(NarratedElementType.TITLE, this.createNarrationMessage());
		if (!this.active) return;

		builder.add(
				NarratedElementType.USAGE,
				Component.translatable(isFocused() ? "narration.slider.usage.focused" : "narration.slider.usage.hovered")
		);
	}

	@Override
	protected MutableComponent createNarrationMessage() {
		return Component.translatable("gui.narrate.slider", this.getMessage());
	}
	// endregion

	protected void setValue(double value) {
		double oldValue = this.value;

		double newValue = value;
		newValue = step <= 0.0 ? newValue : Mth.map(
				Math.round(Mth.map(newValue, min, max, 0, 1) / step) * step,
				0, 1,
				min, max
		);

		this.value = Math.clamp(newValue, min, max);

		if (oldValue != this.value) onValueChanged();
	}

	public static class Builder {
		private final double value;
		private int x;
		private int y;
		private int width = 150;
		private int height = 20;
		private double step;
		private double min = 0.0;
		private double max = 1.0;
		private DoubleConsumer valueChangedListener = value -> {
		};
		private DoubleFunction<Component> messageSupplier = (value) -> Component.nullToEmpty(String.format("%.2f", value));

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

		public Builder message(Component message) {
			messageSupplier = (ignored) -> message;
			return this;
		}

		public Builder message(DoubleFunction<Component> messageSupplier) {
			this.messageSupplier = messageSupplier;
			return this;
		}

		public Builder step(double step) {
			this.step = step;
			return this;
		}

		public Builder range(double min, double max) {
			this.min = min;
			this.max = max;
			return this;
		}

		public Builder onValueChanged(DoubleConsumer valueChangedListener) {
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
					min, max,
					valueChangedListener,
					messageSupplier
			);
		}
	}
}
