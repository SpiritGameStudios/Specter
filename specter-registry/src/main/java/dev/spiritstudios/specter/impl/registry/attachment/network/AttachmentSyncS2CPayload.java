package dev.spiritstudios.specter.impl.registry.attachment.network;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@ApiStatus.Internal
public record AttachmentSyncS2CPayload<V>(AttachmentPair<V> attachmentPair) implements CustomPayload {
	public static final Id<AttachmentSyncS2CPayload<Object>> ID = new Id<>(Identifier.of(SpecterGlobals.MODID, "attachment_sync"));

	private static final Map<Identifier, CacheEntry<?>> CACHE = new Object2ReferenceOpenHashMap<>();

	@SuppressWarnings("unchecked")
	public static PacketCodec<RegistryByteBuf, AttachmentSyncS2CPayload<Object>> CODEC =
		PacketCodec.tuple(
			Identifier.PACKET_CODEC.xmap(
					id -> (Registry<Object>) Registries.ROOT.get(id),
					registry -> registry.getKey().getValue()
				).<RegistryByteBuf>cast()
				.dispatch(
					Attachment::getRegistry,
					registry -> Identifier.PACKET_CODEC.xmap(
						id -> (Attachment<Object, Object>) AttachmentHolder.of(registry).specter$getAttachment(id),
						Attachment::getId
					).cast()
				)
				.dispatch(
					entry -> (Attachment<Object, Object>) entry.attachment,
					AttachmentPair::packetCodec
				),
			AttachmentSyncS2CPayload::attachmentPair,
			AttachmentSyncS2CPayload::new
		);

	private static void fillCache() {
		if (!CACHE.isEmpty()) return;

		for (Registry<?> registry : Registries.ROOT) {
			AttachmentHolder<?> attachmentHolder = AttachmentHolder.of(registry);
			attachmentHolder.specter$getAttachments().forEach(entry -> {
				if (entry.getValue().getSide() == ResourceType.CLIENT_RESOURCES)
					return;

				SpecterGlobals.LOGGER.debug("Caching attachment {}", entry.getKey());
				cacheAttachment(entry.getValue());
			});
		}
	}

	private static <R, V> void cacheAttachment(Attachment<R, V> attachment) {
		Map<String, Set<AttachmentSyncEntry<V>>> encodedEntries = new Object2ObjectOpenHashMap<>();

		for (Attachment.Entry<R, V> entry : attachment) {
			Identifier id = attachment.getRegistry().getId(entry.key());
			if (id == null)
				throw new IllegalStateException("Registry entry " + entry.key() + " has no identifier");

			encodedEntries.computeIfAbsent(id.getNamespace(), identifier -> new HashSet<>()).add(new AttachmentSyncEntry<>(id.getPath(), entry.value()));
		}

		Set<AttachmentPair<V>> attachmentPairs = new HashSet<>();
		for (Map.Entry<String, Set<AttachmentSyncEntry<V>>> entry : encodedEntries.entrySet())
			attachmentPairs.add(new AttachmentPair<>(entry.getKey(), entry.getValue(), attachment));

		CACHE.put(attachment.getId(), new CacheEntry<>(attachmentPairs));
	}

	public static Stream<AttachmentSyncS2CPayload<?>> createPayloads() {
		fillCache();

		return CACHE.values().stream().flatMap(CacheEntry::toPayloads);
	}

	public static void clearCache() {
		CACHE.clear();
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	private record CacheEntry<V>(Set<AttachmentPair<V>> attachmentPairs) {
		Stream<AttachmentSyncS2CPayload<V>> toPayloads() {
			return attachmentPairs.stream().map(AttachmentSyncS2CPayload::new);
		}
	}

	public record AttachmentSyncEntry<V>(String id, V value) {
		public static <V> PacketCodec<RegistryByteBuf, AttachmentSyncEntry<V>> packetCodec(Attachment<?, V> attachment) {
			return PacketCodec.tuple(
				PacketCodecs.STRING,
				AttachmentSyncEntry::id,
				attachment.getPacketCodec(),
				AttachmentSyncEntry::value,
				AttachmentSyncEntry::new
			);
		}
	}

	public record AttachmentPair<V>(String namespace, Set<AttachmentSyncEntry<V>> entries,
									Attachment<?, V> attachment) {
		public static <V> PacketCodec<RegistryByteBuf, AttachmentPair<V>> packetCodec(Attachment<?, V> attachment) {
			return PacketCodec.tuple(
				PacketCodecs.STRING,
				AttachmentPair::namespace,
				PacketCodecs.collection(
					HashSet::newHashSet,
					AttachmentSyncEntry.packetCodec(attachment),
					Integer.MAX_VALUE
				),
				AttachmentPair::entries,
				(namespace, entries) -> new AttachmentPair<>(namespace, entries, attachment)
			);
		}
	}
}
