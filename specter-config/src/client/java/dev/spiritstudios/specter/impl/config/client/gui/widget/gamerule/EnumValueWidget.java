package dev.spiritstudios.specter.impl.config.client.gui.widget.gamerule;


import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.config.client.TabbedListConfigScreen;
import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EnumValueWidget<T extends Enum<T>> extends TabbedListConfigScreen.ValueWidget {
    public static final TabbedListConfigScreen.ValueWidgetFactory<Enum<?>> FACTORY = new TabbedListConfigScreen.ValueWidgetFactory<>() {
        @Override
        public TabbedListConfigScreen.ValueWidget create(MinecraftClient client, String translationPrefix, List<OrderedText> description, Text narration, Text name, Value<Enum<?>> value) {
            return new EnumValueWidget<>(client, translationPrefix, description, narration, name, value);
        }

        @Override
        public Text toString(String translationPrefix, Enum<?> value) {
            return Text.translatable("%s.%s".formatted(translationPrefix, value.toString().toLowerCase()));
        }
    };


    private final CyclingButtonWidget<T> button;
    private final Value<Enum<?>> value;

    public EnumValueWidget(
            MinecraftClient client,
            String translationPrefix,
            @Nullable List<OrderedText> description,
            Text narration,
            Text name,
            Value<Enum<?>> value
    ) {
        super(client, description, name);

        this.value = value;

        this.button = CyclingButtonWidget.<T>builder(val -> FACTORY.toString(translationPrefix, val))
                .values(Arrays.stream(value.defaultValue().getDeclaringClass().getEnumConstants())
                        .map(ReflectionHelper::<T>cast)
                        .flatMap(Optional::stream)
                        .toList())
                .initially(ReflectionHelper.<T>cast(value.get()).orElseThrow())
                .omitKeyText()
                .narration(button ->
                        button.getGenericNarrationMessage().append("\n").append(narration))
                .build(
                        10, 5,
                        44, 20,
                        name
                );

        this.children.add(this.button);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);

        this.button.setX(x + entryWidth - 45);
        this.button.setY(y);
        this.button.render(context, mouseX, mouseY, tickProgress);
    }

    @Override
    public void apply() {
        this.value.set(button.getValue());
    }
}

