package dev.spiritstudios.specter.api.core.debug;

import dev.spiritstudios.specter.impl.core.debug.DebugRendererRegistryImpl;
import dev.spiritstudios.specter.impl.core.debug.ToggleableDebugRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.Identifier;

public final class DebugRendererRegistry {
	public static void register(Identifier id, DebugRenderer.Renderer entry) {
		DebugRendererRegistryImpl.register(id, new ToggleableDebugRenderer(entry));
	}
}
