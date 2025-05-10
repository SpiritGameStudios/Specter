package dev.spiritstudios.specter.api.gui.client.bridget.layout;

import com.google.common.collect.Iterables;
import org.jetbrains.annotations.Nullable;

import dev.spiritstudios.specter.api.gui.client.bridget.Bridget;

public class GridBridget extends LayoutBridget {
	protected int rows, columns;
	protected int gap = 0;

	public GridBridget rows(int rows) {
		this.rows = rows;
		return this;
	}

	public GridBridget columns(int columns) {
		this.columns = columns;
		return this;
	}

	@Override
	public void layoutChildren() {
		int xOffset = 0;
		int yOffset = 0;

		for (int row = 0; row < this.rows; row++) {
			xOffset = 0;
			int rowSize = height / rows;
			for (int col = 0; col < this.columns; col++) {
				Bridget item = child(row, col);
				int colSize = width / columns;
				if (item != null) {

					item.pos(
							xOffset -
									item.horizontalAlign().align(item.width, width) +
									item.horizontalAlign().align(item.width, colSize),
							yOffset -
									item.verticalAlign().align(item.height, height) +
									item.verticalAlign().align(item.height, rowSize)
					);
				}
				xOffset += colSize + gap;
			}
			yOffset += rowSize + gap;
		}
	}

	private @Nullable Bridget child(int row, int col) {
		return Iterables.get(children, row * columns + col, null);
	}
}
