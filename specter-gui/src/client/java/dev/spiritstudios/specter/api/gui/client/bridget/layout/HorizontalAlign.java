package dev.spiritstudios.specter.api.gui.client.bridget.layout;

import java.util.function.BiFunction;

public enum HorizontalAlign {
	LEFT((itemWidth, span) -> 0),
	RIGHT((itemWidth, span) -> span - itemWidth),
	CENTER((itemWidth, span) -> span / 2 - itemWidth / 2);

	private final BiFunction<Integer, Integer, Integer> align;

	HorizontalAlign(BiFunction<Integer, Integer, Integer> align) {
		this.align = align;
	}

	public int align(int itemWidth, int span) {
		return align.apply(itemWidth, span);
	}
}
