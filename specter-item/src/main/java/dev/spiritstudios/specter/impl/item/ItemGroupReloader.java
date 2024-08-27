package dev.spiritstudios.specter.impl.item;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.item.DataItemGroup;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.spiritstudios.specter.api.core.SpecterGlobals.MODID;

public class ItemGroupReloader implements SimpleSynchronousResourceReloadListener {
	public static final List<DataItemGroup> ITEM_GROUPS = new ArrayList<>();
	public static boolean RELOADED = false;

	@Override
	public Identifier getFabricId() {
		return Identifier.of(MODID, "item_group");
	}

	@Override
	public void reload(ResourceManager manager) {
		ITEM_GROUPS.clear();

		Map<Identifier, Resource> resources = manager.findResources(
			"item_group",
			string -> string.getPath().endsWith(".json")
		);

		for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
			Resource resource = entry.getValue();

			try (InputStreamReader resourceReader = new InputStreamReader(resource.getInputStream())) {
				JsonObject resourceJson = JsonHelper.deserialize(resourceReader);
				DataItemGroup group = DataItemGroup.CODEC.parse(JsonOps.INSTANCE, resourceJson).getOrThrow();

				ITEM_GROUPS.add(group);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		RELOADED = true;
	}
}
