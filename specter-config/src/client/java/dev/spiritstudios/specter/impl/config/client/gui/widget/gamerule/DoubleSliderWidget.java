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
import dev.spiritstudios.specter.api.config.gui.DoubleSliderHint;
import dev.spiritstudios.specter.api.gui.client.widget.SpecterSliderWidget;
import dev.spiritstudios.specter.impl.config.DoubleRangeConstraint;

public class DoubleSliderWidget extends TabbedListConfigScreen.ValueWidget {
	private final SpecterSliderWidget slider;
	private final Value<Double> value;

	public DoubleSliderWidget(
			MinecraftClient client,
			@Nullable List<OrderedText> description,
			Text narration,
			Text name,
			Value<Double> value,
			DoubleSliderHint sliderHint
	) {
		super(client, description, name);

		this.value = value;

		double step = sliderHint.step();

		Optional<DoubleRangeConstraint> range = value.constraint(DoubleRangeConstraint.class);

		double min = range.map(DoubleRangeConstraint::min).orElse(Double.MIN_VALUE);
		double max = range.map(DoubleRangeConstraint::max).orElse(Double.MAX_VALUE);

		this.slider = new SpecterSliderWidget.Builder(val -> Text.literal("%.2f".formatted(val)))
				.initially(value.get())
				.range(min, max)
				.step(step)
				.omitKeyText()
				.build(
						10, 5,
						44, 20,
						name
				);


		this.children.add(this.slider);
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);

		this.slider.setX(x + entryWidth - 45);
		this.slider.setY(y);
		this.slider.render(context, mouseX, mouseY, tickProgress);
	}

	@Override
	public void apply() {
		this.value.set(slider.getValue());
	}
}

