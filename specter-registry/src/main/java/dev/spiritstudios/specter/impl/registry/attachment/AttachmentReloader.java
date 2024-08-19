package dev.spiritstudios.specter.impl.registry.attachment;

import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.core.Specter;
import dev.spiritstudios.specter.impl.registry.SpecterRegistry;
import dev.spiritstudios.specter.impl.registry.attachment.network.AttachmentSyncS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static dev.spiritstudios.specter.impl.core.Specter.MODID;

public class AttachmentReloader implements SimpleResourceReloadListener<HashMap<Attachment<?, ?>, AttachmentMap<?, ?>>> {
	private void processAttachment(Map<Identifier, List<Resource>> resources, Registry<?> registry, HashMap<Attachment<?, ?>, AttachmentMap<?, ?>> attachmentMaps) {
		for (Map.Entry<Identifier, List<Resource>> entry : resources.entrySet()) {
			Identifier attachmentResourceId = entry.getKey();
			String path = attachmentResourceId.getPath();
			path = path.substring(path.lastIndexOf('/') + 1);
			path = path.substring(0, path.lastIndexOf('.'));

			Identifier attachmentId = Identifier.of(attachmentResourceId.getNamespace(), path);

			List<Resource> attachmentResources = entry.getValue();

			Attachment<?, ?> attachment = AttachmentHolder.of(registry).specter$getAttachment(attachmentId);
			if (attachment == null) continue;

			AttachmentMap<?, ?> map = attachmentMaps.computeIfAbsent(attachment, this::createMap);
			for (Resource attachmentResource : attachmentResources)
				map.processResource(attachmentId, attachmentResource);
		}
	}

	private <R, V> AttachmentMap<R, V> createMap(Attachment<R, V> attachment) {
		return new AttachmentMap<>(attachment.getRegistry(), attachment);
	}

	@Override
	public CompletableFuture<HashMap<Attachment<?, ?>, AttachmentMap<?, ?>>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Specter.LOGGER.info("owo loaded attachments");
			HashMap<Attachment<?, ?>, AttachmentMap<?, ?>> attachmentMaps = new HashMap<>();

			for (RegistryEntry<MutableRegistry<?>> entry : Registries.ROOT.getIndexedEntries()) {
				if (entry.getKey().isEmpty()) continue;
				Identifier id = entry.getKey().get().getValue();
				String path = id.getNamespace() + "/" + id.getPath();

				Map<Identifier, List<Resource>> resources = manager.findAllResources("attachments/" + path, string -> string.getPath().endsWith(".json"));
				if (resources.isEmpty()) continue;

				Registry<?> registry = entry.value();
				processAttachment(resources, registry, attachmentMaps);
			}

			return attachmentMaps;
		}, executor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletableFuture<Void> apply(HashMap<Attachment<?, ?>, AttachmentMap<?, ?>> data, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (RegistryEntry<MutableRegistry<?>> entry : Registries.ROOT.getIndexedEntries()) {
				AttachmentHolder<?> holder = AttachmentHolder.of(entry.value());
				holder.specter$getValues().clear();
			}

			for (Map.Entry<Attachment<?, ?>, AttachmentMap<?, ?>> entry : data.entrySet()) {
				Attachment<?, ?> attachment = entry.getKey();
				AttachmentMap<?, ?> map = entry.getValue();

				applyAttachment((Attachment<Object, Object>) attachment, (AttachmentMap<Object, Object>) map);
			}

			AttachmentSyncS2CPayload.clearCache();
			MinecraftServer server = SpecterRegistry.getServer();
			if (server == null) return;
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				AttachmentSyncS2CPayload.createPayloads().forEach(payload -> ServerPlayNetworking.send(player, payload));
			}
		}, executor);
	}

	@SuppressWarnings("unchecked")
	private <R, V> void applyAttachment(Attachment<R, V> attachment, AttachmentMap<R, V> map) {
		Registry<R> registry = attachment.getRegistry();

		for (Map.Entry<Identifier, Object> entry : map.getValues().entrySet()) {
			Identifier id = entry.getKey();
			Object value = entry.getValue();

			attachment.put(registry.get(id), (V) value);
		}
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.of(MODID, "attachments");
	}
}
