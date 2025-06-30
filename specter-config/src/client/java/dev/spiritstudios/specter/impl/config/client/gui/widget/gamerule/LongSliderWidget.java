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
import dev.spiritstudios.specter.api.config.gui.LongSliderHint;
import dev.spiritstudios.specter.api.gui.client.widget.SpecterSliderWidget;
import dev.spiritstudios.specter.impl.config.LongRangeConstraint;

public class LongSliderWidget extends TabbedListConfigScreen.ValueWidget {
	private final SpecterSliderWidget slider;
	private final Value<Long> value;

	public LongSliderWidget(
			MinecraftClient client,
			@Nullable List<OrderedText> description,
			Text narration,
			Text name,
			Value<Long> value,
			LongSliderHint sliderHint
	) {
		super(client, description, name);

		this.value = value;

		Optional<LongRangeConstraint> range = value.constraint(LongRangeConstraint.class);

		long min = range.map(LongRangeConstraint::min).orElse(Long.MIN_VALUE);
		long max = range.map(LongRangeConstraint::max).orElse(Long.MAX_VALUE);

		this.slider = new SpecterSliderWidget.Builder(val -> Text.literal(String.valueOf((int) val)))
				.initially(value.get())
				.range(min, max)
				.step(sliderHint.step())
				.omitKeyText()
				.build(
						10, 5,
						44, 20,
						name
				);
		
		this.children.add(this.slider);
	}

	@Override
	public void apply() {
		this.value.set((long) this.slider.getValue());
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
		super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);

		this.slider.setX(x + entryWidth - 45);
		this.slider.setY(y);
		this.slider.render(context, mouseX, mouseY, tickProgress);
	}
}

