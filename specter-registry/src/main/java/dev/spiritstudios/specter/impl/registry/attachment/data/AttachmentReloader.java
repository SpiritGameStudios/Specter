package dev.spiritstudios.specter.impl.registry.attachment.data;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.registry.SpecterRegistry;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import dev.spiritstudios.specter.impl.registry.attachment.network.AttachmentSyncS2CPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AttachmentReloader implements SimpleResourceReloadListener<Map<Attachment<?, ?>, AttachmentMap<?, ?>>> {
	private final ResourceType side;

	public AttachmentReloader(ResourceType side) {
		this.side = side;
	}

	private <R, V> AttachmentMap<R, V> createMap(Attachment<R, V> attachment) {
		return new AttachmentMap<>(attachment.getRegistry(), attachment);
	}

	@Override
	public CompletableFuture<Map<Attachment<?, ?>, AttachmentMap<?, ?>>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Attachment<?, ?>, AttachmentMap<?, ?>> attachmentMaps = new Object2ObjectOpenHashMap<>();

			for (RegistryEntry<MutableRegistry<?>> entry : Registries.ROOT.getIndexedEntries()) { // For each registry
				if (entry.getKey().isEmpty()) continue;
				Identifier id = entry.getKey().get().getValue();
				String attachmentPath = id.getNamespace() + "/" + id.getPath();

				Map<Identifier, List<Resource>> attachmentResources = manager.findAllResources(
					"attachments/" + attachmentPath,
					string -> string.getPath().endsWith(".json")
				);

				if (attachmentResources.isEmpty()) continue;

				Registry<?> registry = entry.value();
				parseAttachmentResources(attachmentResources, registry, attachmentMaps);
			}

			return attachmentMaps;
		}, executor);
	}

	private void parseAttachmentResources(Map<Identifier, List<Resource>> resources, Registry<?> registry, Map<Attachment<?, ?>, AttachmentMap<?, ?>> attachmentMaps) {
		for (Map.Entry<Identifier, List<Resource>> resource : resources.entrySet()) {
			Identifier attachmentResourceId = resource.getKey();

			// Transform the path to the attachment id (e.g. specter:attachments/minecraft/block/strippable.json -> specter:strippable
			String path = attachmentResourceId.getPath();
			path = path.substring(path.lastIndexOf('/') + 1);
			path = path.substring(0, path.lastIndexOf('.'));
			Identifier attachmentId = Identifier.of(attachmentResourceId.getNamespace(), path);

			List<Resource> attachmentResources = resource.getValue();
			Attachment<?, ?> attachment = AttachmentHolder.of(registry).specter$getAttachment(attachmentId);
			if (attachment == null) continue;
			if (attachment.getSide() != this.side) continue;

			AttachmentMap<?, ?> map = attachmentMaps.computeIfAbsent(attachment, this::createMap);
			for (Resource attachmentResource : attachmentResources)
				map.parseResource(attachmentId, attachmentResource);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletableFuture<Void> apply(Map<Attachment<?, ?>, AttachmentMap<?, ?>> data, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (Map.Entry<Attachment<?, ?>, AttachmentMap<?, ?>> entry : data.entrySet()) {
				Attachment<?, ?> attachment = entry.getKey();
				AttachmentMap<?, ?> map = entry.getValue();

				loadAttachment((Attachment<Object, Object>) attachment, (AttachmentMap<Object, Object>) map);
			}

			AttachmentSyncS2CPayload.clearCache();

			if (this.side != ResourceType.SERVER_DATA) return;
			MinecraftServer server = SpecterRegistry.getServer();
			if (server == null) return;
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
				AttachmentSyncS2CPayload.createPayloads().forEach(payload -> ServerPlayNetworking.send(player, payload));
		}, executor);
	}

	private <R, V> void loadAttachment(Attachment<R, V> attachment, AttachmentMap<R, V> map) {
		Registry<R> registry = attachment.getRegistry();

		AttachmentHolder<R> holder = AttachmentHolder.of(registry);
		if (attachment.getSide() == this.side)
			holder.specter$clearAttachment(attachment);

		for (Map.Entry<Identifier, V> entry : map.getValues().entrySet()) {
			Identifier id = entry.getKey();
			V value = entry.getValue();

			attachment.put(registry.get(id), value);
		}
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.of(SpecterGlobals.MODID, this.side == ResourceType.SERVER_DATA ? "attachments_data" : "attachments_resources");
	}
}
