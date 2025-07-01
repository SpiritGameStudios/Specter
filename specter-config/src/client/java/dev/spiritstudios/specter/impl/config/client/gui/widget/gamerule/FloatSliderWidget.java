package dev.spiritstudios.specter.impl.config.client.gui.widget.gamerule;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.config.client.TabbedListConfigScreen;
import dev.spiritstudios.specter.api.config.gui.FloatSliderHint;
import dev.spiritstudios.specter.api.gui.client.widget.SpecterSliderWidget;
import dev.spiritstudios.specter.impl.config.FloatRangeConstraint;

public class FloatSliderWidget extends TabbedListConfigScreen.ValueWidget {
	private final SpecterSliderWidget slider;
	private final Value<Float> value;

	public FloatSliderWidget(
			MinecraftClient client,
			@Nullable List<OrderedText> description,
			Text narration,
			Text name,
			Value<Float> value,
			FloatSliderHint sliderHint
	) {
		super(client, description, name);

		this.value = value;

		float step = sliderHint.step();

		Optional<FloatRangeConstraint> range = value.constraint(FloatRangeConstraint.class);

		float min = range.map(FloatRangeConstraint::min).orElse(Float.MIN_VALUE);
		float max = range.map(FloatRangeConstraint::max).orElse(Float.MAX_VALUE);

		this.slider = new SpecterSliderWidget.Builder(val -> Text.literal("%.1f".formatted(val)))
				.initially(value.get())
				.range(min, max)
				.step(step)
				.omitKeyText()
				.build(
						10, 5,
						160, 20,
						name
				);


		this.children.add(this.slider);
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);

		this.slider.setX(x + entryWidth - 165);
		this.slider.setY(y);
		this.slider.render(context, mouseX, mouseY, tickProgress);
	}

	@Override
	public void apply() {
		this.value.set((float) slider.getValue());
	}
}
