package dev.spiritstudios.specter.api.gui.client.bridget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Bridget implements Drawable {
	public boolean mayContain;
	public boolean overrideDimensions;
	public List<Bridget> children = new ArrayList<>();
	public Vector2i position;
	public Vector2i dimensions;

	public Bridget(int x, int y, int width, int height, boolean overrideDimensions, boolean mayContain) {
		this.mayContain = mayContain;
		this.overrideDimensions = overrideDimensions;
		this.position = new Vector2i(x, y);
		this.dimensions = new Vector2i(width, height);
	}

	// Builder

	public static class Builder {
		public int x = 0;
		public int y = 0;
		public int width = 0;
		public int height = 0;
		public boolean overrideDimensions = false;
		public boolean mayContainChildren = true;
		public Bridget type;

		public Builder setType(Bridget type) {
			return this;
		}

		public Builder setX(int x) {
			this.x = x;
			return this;
		}

		public Builder setY(int y) {
			this.y = y;
			return this;
		}

		public Builder setWidth(int width) {
			this.width = width;
			return this;
		}

		public Builder setHeight(int height) {
			this.height = height;
			return this;
		}

		public Builder mayOverrideDimensions(boolean overrideDimensions) {
			this.overrideDimensions = overrideDimensions;
			return this;
		}

		public Builder mayContainChildren(boolean mayContainChildren) {
			this.mayContainChildren = mayContainChildren;
			return this;
		}

		public Bridget build() {
			return new Bridget(x, y, width, height, overrideDimensions, mayContainChildren);
		}
	}

	// Dimensions & Position
	// Just steal this stuff by implementing/extending Widget.

	public void setPosition(int x, int y) {
		this.position.x = x;
		this.position.y = y;
	}

	public void setX(int x) {
		this.position.x = x;
	}

	public int getX() {
		return this.position.x;
	}

	public void setY(int y) {
		this.position.y = y;
	}

	public int getY() {
		return this.position.y;
	}

	public void setDimensions(int width, int height) {
		this.dimensions.x = width;
		this.dimensions.y = height;
	}

	public void setWidth(int width) {
		this.dimensions.x = width;
	}

	public int getWidth() {
		return this.dimensions.x;
	}

	public void setHeight(int height) {
		this.dimensions.y = height;
	}

	public int getHeight() {
		return this.dimensions.y;
	}

	// Children

	public void addChild(Bridget child) {
		if (this.mayContain) {
			this.children.add(child);
		}
	}

	public void removeChild(Bridget child) {
		this.children.remove(child);
	}

	public void clearChildren() {
		this.children.clear();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
	}
}
