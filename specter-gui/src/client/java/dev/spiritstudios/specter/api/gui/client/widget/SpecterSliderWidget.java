package dev.spiritstudios.specter.api.gui.client.widget;

import java.util.function.Function;

import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SpecterSliderWidget extends ClickableWidget {
	private static final Identifier TEXTURE = Identifier.ofVanilla("widget/slider");
	private static final Identifier HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/slider_highlighted");
	private static final Identifier HANDLE_TEXTURE = Identifier.ofVanilla("widget/slider_handle");
	private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/slider_handle_highlighted");

	private final Double2ObjectFunction<Text> valueToText;
	private final Function<SpecterSliderWidget, MutableText> narrationMessageFactory;

	private final UpdateCallback callback;

	private final boolean optionTextOmitted;
	private final SimpleOption.TooltipFactory<Double> tooltipFactory;
	private final Text optionText;
	private final double min;
	private final double max;
	private final double step;
	private double value;
	private boolean sliderFocused;

	protected SpecterSliderWidget(
			int x, int y,
			int width, int height,
			Text message,
			Text optionText,
			double initialValue,
			Double2ObjectFunction<Text> valueToText,
			Function<SpecterSliderWidget, MutableText> narrationMessageFactory,
			UpdateCallback callback,
			boolean optionTextOmitted,
			SimpleOption.TooltipFactory<Double> tooltipFactory, double min, double max, double step
	) {
		super(x, y, width, height, message);
		this.valueToText = valueToText;
		this.narrationMessageFactory = narrationMessageFactory;
		this.callback = callback;
		this.optionTextOmitted = optionTextOmitted;
		this.tooltipFactory = tooltipFactory;
		this.optionText = optionText;
		this.value = initialValue;
		this.min = min;
		this.max = max;
		this.step = step;
	}

	private void refreshTooltip() {
		this.setTooltip(this.tooltipFactory.apply(this.value));
	}

	// region Rendering
	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		MinecraftClient client = MinecraftClient.getInstance();

		context.drawGuiTexture(
				RenderLayer::getGuiTextured, this.getTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorHelper.getWhite(this.alpha)
		);


		context.drawGuiTexture(
				RenderLayer::getGuiTextured,
				this.getHandleTexture(),
				this.getX() + (int) MathHelper.map(this.value, min, max, 0, this.width - 8),
				this.getY(),
				8,
				this.getHeight(),
				ColorHelper.getWhite(this.alpha)
		);

		this.drawScrollableText(
				context,
				client.textRenderer,
				2,
				this.active ? 0xffffff : 0xa0a0a0 | MathHelper.ceil(this.alpha * 255.0F) << 24
		);
	}

	private Identifier getTexture() {
		return this.isNarratable() && this.isFocused() && !this.sliderFocused ? HIGHLIGHTED_TEXTURE : TEXTURE;
	}

	private Identifier getHandleTexture() {
		return !this.isNarratable() || !this.hovered && !this.sliderFocused ? HANDLE_TEXTURE : HANDLE_HIGHLIGHTED_TEXTURE;
	}
	// endregion

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		value = MathHelper.clamp(value, min, max);
		Text text = this.composeText(value);
		this.setMessage(text);
		this.value = value;
		this.refreshTooltip();
		this.callback.onValueChange(this, this.value);
	}

	// region Input
	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			this.sliderFocused = false;
		} else {
			GuiNavigationType guiNavigationType = MinecraftClient.getInstance().getNavigationType();
			if (guiNavigationType == GuiNavigationType.MOUSE || guiNavigationType == GuiNavigationType.KEYBOARD_TAB) {
				this.sliderFocused = true;
			}
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (KeyCodes.isToggle(keyCode)) {
			this.sliderFocused = !this.sliderFocused;
			return true;
		}

		if (this.sliderFocused) {
			boolean left = keyCode == GLFW.GLFW_KEY_LEFT;
			if (left || keyCode == GLFW.GLFW_KEY_RIGHT) {
				this.setValue(this.value + step * (left ? -1.0F : 1.0F));
				return true;
			}
		}

		return false;
	}

	public void onClick(double mouseX, double mouseY) {
		this.setValueFromMouse(mouseX);
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		this.setValueFromMouse(mouseX);
		super.onDrag(mouseX, mouseY, deltaX, deltaY);
	}

	private void setValueFromMouse(double mouseX) {
		setValue(Math.clamp(min + (step * Math.round((((mouseX - (double) (this.getX() + 4)) / (double) (this.getWidth() - 8)) * (max - min)) / step)), min, max));
	}
	// endregion

	// region Narration
	@Override
	public void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getNarrationMessage());

		if (this.active) {
			builder.put(
					NarrationPart.USAGE,
					this.isFocused() ?
							Text.translatable("narration.slider.usage.focused") :
							Text.translatable("narration.slider.usage.hovered")
			);
		}
	}

	private Text composeText(double value) {
		return this.optionTextOmitted ? this.valueToText.apply(value) : this.composeGenericOptionText(value);
	}

	private MutableText composeGenericOptionText(double value) {
		return ScreenTexts.composeGenericOptionText(this.optionText, this.valueToText.apply(value));
	}

	public MutableText getGenericNarrationMessage() {
		return getNarrationMessage(this.optionTextOmitted ? this.composeGenericOptionText(this.value) : this.getMessage());
	}

	@Override
	protected MutableText getNarrationMessage() {
		return this.narrationMessageFactory.apply(this);
	}
	// endregion

	@Override
	public void playDownSound(SoundManager soundManager) {
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		super.playDownSound(MinecraftClient.getInstance().getSoundManager());
	}

	@Environment(EnvType.CLIENT)
	public interface UpdateCallback {
		void onValueChange(SpecterSliderWidget slider, double value);
	}

	public static class Builder {
		private final Double2ObjectFunction<Text> valueToText;
		private double value;
		private SimpleOption.TooltipFactory<Double> tooltipFactory = value -> null;
		private Function<SpecterSliderWidget, MutableText> narrationMessageFactory = SpecterSliderWidget::getGenericNarrationMessage;
		private boolean optionTextOmitted;
		private double min;
		private double max;
		private double step;

		public Builder(Double2ObjectFunction<Text> valueToText) {
			this.valueToText = valueToText;
		}

		public Builder tooltip(SimpleOption.TooltipFactory<Double> tooltipFactory) {
			this.tooltipFactory = tooltipFactory;
			return this;
		}

		public Builder initially(double value) {
			this.value = value;
			return this;
		}

		public Builder narration(Function<SpecterSliderWidget, MutableText> narrationMessageFactory) {
			this.narrationMessageFactory = narrationMessageFactory;
			return this;
		}

		public Builder omitKeyText() {
			this.optionTextOmitted = true;
			return this;
		}

		public Builder range(double min, double max) {
			this.min = min;
			this.max = max;
			return this;
		}

		public Builder step(double step) {
			this.step = step;
			return this;
		}

		public SpecterSliderWidget build(Text optionText, UpdateCallback callback) {
			return this.build(0, 0, 150, 20, optionText, callback);
		}

		public SpecterSliderWidget build(int x, int y, int width, int height, Text optionText) {
			return this.build(x, y, width, height, optionText, (button, value) -> {
			});
		}

		public SpecterSliderWidget build(int x, int y, int width, int height, Text optionText, UpdateCallback callback) {
			Text text = this.valueToText.apply(value);
			Text message = this.optionTextOmitted ? text : ScreenTexts.composeGenericOptionText(optionText, text);

			return new SpecterSliderWidget(
					x,
					y,
					width,
					height,
					message,
					optionText,
					value,
					this.valueToText,
					this.narrationMessageFactory,
					callback,
					this.optionTextOmitted,
					this.tooltipFactory,
					min, max, step
			);
		}
	}
}
