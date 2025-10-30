package dev.spiritstudios.specter.impl.config.client.gui.widget;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public class OptionsScrollableWidget extends ContainerObjectSelectionList<OptionsScrollableWidget.OptionEntry> {
	public OptionsScrollableWidget(Minecraft client, int width, int height, int y, int itemHeight) {
		super(client, width, height, y, itemHeight);
		this.centerListVertically = false;
	}

	@Override
	protected int scrollBarX() {
		return super.scrollBarX() + 32;
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	public void addOptions(List<AbstractWidget> options) {
		for (int i = 0; i < options.size(); i += 2) {
			AbstractWidget widget = options.get(i);
			AbstractWidget widget2 = i + 1 < options.size() ? options.get(i + 1) : null;

			this.addEntry(new OptionEntry(widget, widget2, this.width));
		}
	}

	protected static class OptionEntry extends Entry<OptionEntry> {
		private final List<AbstractWidget> widgets = new ArrayList<>();

		public OptionEntry(AbstractWidget widget, @Nullable AbstractWidget widget2, int width) {
			widget.setWidth(310);
			if (widget2 != null) {
				widget2.setWidth(150);
				widget.setWidth(150);
			}

			widget.setX(width / 2 - 155);
			if (widget2 != null) widget2.setX(width / 2 + 5);

			this.widgets.add(widget);
			if (widget2 != null) this.widgets.add(widget2);
		}

		@Override
		public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			widgets.forEach(widget -> {
				widget.setY(0);
				widget.render(context, mouseX, mouseY, deltaTicks);
			});
		}

		@Override
		public @NotNull List<? extends NarratableEntry> narratables() {
			return widgets;
		}

		@Override
		public @NotNull List<? extends GuiEventListener> children() {
			return widgets;
		}
	}
}
