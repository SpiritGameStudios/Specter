package dev.spiritstudios.specter.impl.core.debug;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class DebugRendererRegistryImpl {
	private static final Map<Identifier, ToggleableDebugRenderer> RENDERERS = new Object2ObjectOpenHashMap<>();

	public static void register(Identifier id, ToggleableDebugRenderer entry) {
		RENDERERS.put(id, entry);
	}

	public static Map<Identifier, ToggleableDebugRenderer> getRenderers() {
		return ImmutableMap.copyOf(RENDERERS);
	}
}
