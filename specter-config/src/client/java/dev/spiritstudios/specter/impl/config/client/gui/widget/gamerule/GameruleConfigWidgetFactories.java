package dev.spiritstudios.specter.impl.config.client.gui.widget.gamerule;

import dev.spiritstudios.specter.api.config.client.TabbedListConfigScreen;
import dev.spiritstudios.specter.api.config.gui.DoubleSliderHint;
import dev.spiritstudios.specter.api.config.gui.FloatSliderHint;
import dev.spiritstudios.specter.api.config.gui.IntSliderHint;
import dev.spiritstudios.specter.api.config.gui.LongSliderHint;

public final class GameruleConfigWidgetFactories {
	public static final TabbedListConfigScreen.ValueWidgetFactory<Long> LONG = (client, translationPrefix, description, narration, name, value) -> value.hint(LongSliderHint.class)
			.<TabbedListConfigScreen.ValueWidget>map(sliderHint ->
					new LongSliderWidget(client, description, narration, name, value, sliderHint))
			.orElseGet(() -> new LongInputWidget(client, description, narration, name, value));


	public static final TabbedListConfigScreen.ValueWidgetFactory<Integer> INTEGER = (client, translationPrefix, description, narration, name, value) -> value.hint(IntSliderHint.class)
			.<TabbedListConfigScreen.ValueWidget>map(sliderHint ->
					new IntegerSliderWidget(client, description, narration, name, value, sliderHint))
			.orElseGet(() -> new IntegerInputWidget(client, description, narration, name, value));

	public static final TabbedListConfigScreen.ValueWidgetFactory<Float> FLOAT = (client, translationPrefix, description, narration, name, value) -> value.hint(FloatSliderHint.class)
			.<TabbedListConfigScreen.ValueWidget>map(sliderHint ->
					new FloatSliderWidget(client, description, narration, name, value, sliderHint))
			.orElseGet(() -> new FloatInputWidget(client, description, narration, name, value));

	public static final TabbedListConfigScreen.ValueWidgetFactory<Double> DOUBLE = (client, translationPrefix, description, narration, name, value) -> value.hint(DoubleSliderHint.class)
			.<TabbedListConfigScreen.ValueWidget>map(sliderHint ->
					new DoubleSliderWidget(client, description, narration, name, value, sliderHint))
			.orElseGet(() -> new DoubleInputWidget(client, description, narration, name, value));

	public static final TabbedListConfigScreen.ValueWidgetFactory<String> STRING = StringInputWidget::new;
}
