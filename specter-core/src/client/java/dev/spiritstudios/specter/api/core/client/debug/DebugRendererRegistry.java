package dev.spiritstudios.specter.api.core.client.debug;

import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.impl.core.client.debug.DebugRendererRegistryImpl;
import dev.spiritstudios.specter.impl.core.client.debug.ToggleableDebugRenderer;

/**
 * Allows you to register custom debug renderers that can be toggled with the <code>/debugrender</code> command.
 *
 * @see DebugRenderer.Renderer
 */
public final class DebugRendererRegistry {
	public static void register(Identifier id, DebugRenderer.Renderer entry) {
		DebugRendererRegistryImpl.register(id, new ToggleableDebugRenderer(entry));
	}
}
