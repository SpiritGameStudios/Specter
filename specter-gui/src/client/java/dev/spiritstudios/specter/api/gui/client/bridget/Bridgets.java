package dev.spiritstudios.specter.api.gui.client.bridget;

import net.minecraft.text.Text;

public final class Bridgets {
	public static BackgroundBridget background(int width, int height) {
		BackgroundBridget background = new BackgroundBridget();
		background.size(width, height);
		return background;
	}

	public static SolidColorBridget color(int width, int height) {
		SolidColorBridget background = new SolidColorBridget();
		background.size(width, height);
		return background;
	}

	public static SolidColorBridget black(int width, int height) {
		SolidColorBridget background = new SolidColorBridget();
		background.color = 0xFF000000;
		background.size(width, height);
		return background;
	}

	public static TextBridget text(Text content) {
		TextBridget background = new TextBridget();
		background.content(content);
		return background;
	}

	public static TextBridget text(String content) {
		TextBridget background = new TextBridget();
		background.content(content);
		return background;
	}
}
