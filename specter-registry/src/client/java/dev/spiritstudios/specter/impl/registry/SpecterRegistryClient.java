package dev.spiritstudios.specter.impl.registry;

import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import dev.spiritstudios.specter.impl.registry.attachment.data.AttachmentReloader;
import dev.spiritstudios.specter.impl.registry.attachment.network.AttachmentSyncS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class SpecterRegistryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new AttachmentReloader(ResourceType.CLIENT_RESOURCES));

		ClientPlayNetworking.registerGlobalReceiver(AttachmentSyncS2CPayload.ID, (payload, context) -> context.client().execute(() -> applyAttachmentSync(payload)));
	}

	@SuppressWarnings("unchecked")
	private static <V> void applyAttachmentSync(AttachmentSyncS2CPayload<V> payload) {
		if (MinecraftClient.getInstance().isIntegratedServerRunning())
			return;

		Attachment<Object, V> attachment = (Attachment<Object, V>) payload.attachmentPair().attachment();
		Registry<Object> registry = attachment.getRegistry();
		AttachmentHolder<Object> attachmentHolder = AttachmentHolder.of(registry);

		attachmentHolder.specter$getValues().row(attachment).clear();

		for (AttachmentSyncS2CPayload.AttachmentSyncEntry<V> entry : payload.attachmentPair().entries()) {
			Identifier id = Identifier.of(payload.attachmentPair().namespace(), entry.id());
			Object object = registry.get(id);
			if (object == null)
				throw new IllegalStateException("Entry " + id + " is not in the registry");

			V value = entry.value();
			attachmentHolder.specter$putAttachmentValue(attachment, object, value);
		}
	}
}
