package dev.spiritstudios.specter.api.gui.client.bridget;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

import dev.spiritstudios.specter.api.gui.client.bridget.layout.HorizontalAlign;
import dev.spiritstudios.specter.api.gui.client.bridget.layout.VerticalAlign;

public class Bridget implements Drawable {
	protected final MinecraftClient client = MinecraftClient.getInstance();

	public int width = 0;
	public int height = 0;

	protected List<Bridget> children = new ArrayList<>();
	protected @Nullable Bridget parent;

	protected HorizontalAlign horizontalAlign = HorizontalAlign.CENTER;
	protected VerticalAlign verticalAlign = VerticalAlign.CENTER;

	private int x = 0, y = 0;

	// region Children
	public Bridget child(Bridget child) {
		this.children.add(child);
		child.parent = this;
		return this;
	}

	public void removeChild(Bridget child) {
		this.children.remove(child);
	}

	public void clearChildren() {
		this.children.clear();
	}
	// endregion

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		for (Bridget child : children) child.render(context, mouseX, mouseY, delta);
	}

	public Bridget x(int x) {
		this.x = x;
		return this;
	}

	public int x() {
		return parent != null ?
				this.x + parent.x() + horizontalAlign.align(width, parent.width) :
				this.x;
	}

	public Bridget y(int y) {
		this.y = y;
		return this;
	}

	public int y() {
		return parent != null ?
				this.y + parent.y() + verticalAlign.align(height, parent.height) :
				this.y;
	}

	public Bridget pos(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}


	public Bridget width(int width) {
		this.width = width;
		return this;
	}

	public Bridget height(int height) {
		this.height = height;
		return this;
	}

	public Bridget size(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public Bridget horizontalAlign(HorizontalAlign horizontalAlign) {
		this.horizontalAlign = horizontalAlign;
		return this;
	}

	public HorizontalAlign horizontalAlign() {
		return horizontalAlign;
	}

	public Bridget verticalAlign(VerticalAlign verticalAlign) {
		this.verticalAlign = verticalAlign;
		return this;
	}

	public VerticalAlign verticalAlign() {
		return verticalAlign;
	}
}
