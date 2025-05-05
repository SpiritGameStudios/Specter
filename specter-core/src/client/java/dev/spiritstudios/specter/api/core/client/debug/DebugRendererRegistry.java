package dev.spiritstudios.specter.api.core.client.debug;

import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.impl.core.client.debug.DebugRendererRegistryImpl;
import dev.spiritstudios.specter.impl.core.client.debug.ToggleableDebugRenderer;

public final class DebugRendererRegistry {
	public static void register(Identifier id, DebugRenderer.Renderer entry) {
		DebugRendererRegistryImpl.register(id, new ToggleableDebugRenderer(entry));
	}
}
