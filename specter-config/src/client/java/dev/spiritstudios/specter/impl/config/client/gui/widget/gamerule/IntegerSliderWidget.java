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
import dev.spiritstudios.specter.api.config.gui.IntSliderHint;
import dev.spiritstudios.specter.api.gui.client.widget.SpecterSliderWidget;
import dev.spiritstudios.specter.impl.config.IntegerRangeConstraint;

public class IntegerSliderWidget extends TabbedListConfigScreen.ValueWidget {
	private final SpecterSliderWidget slider;
	private final Value<Integer> value;

	public IntegerSliderWidget(
			MinecraftClient client,
			@Nullable List<OrderedText> description,
			Text narration,
			Text name,
			Value<Integer> value,
			IntSliderHint sliderHint
	) {
		super(client, description, name);

		this.value = value;

		int step = sliderHint.step();

		Optional<IntegerRangeConstraint> range = value.constraint(IntegerRangeConstraint.class);

		int min = range.map(IntegerRangeConstraint::min).orElse(Integer.MIN_VALUE);
		int max = range.map(IntegerRangeConstraint::max).orElse(Integer.MAX_VALUE);

		this.slider = new SpecterSliderWidget.Builder(val -> Text.literal(String.valueOf((int) val)))
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
		this.value.set((int) slider.getValue());
	}
}
