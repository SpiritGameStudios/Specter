package dev.spiritstudios.specter.impl.registry.metatag.network;

import com.mojang.datafixers.util.Pair;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.util.SpecterPacketCodecs;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public record MetatagSyncS2CPayload<R, V>(Metatag<R, V> metatag, List<Pair<R, V>> values) implements CustomPayload {
	public static final Id<MetatagSyncS2CPayload<?, ?>> ID = new Id<>(Identifier.of(SpecterGlobals.MODID, "metatag_sync"));
	public static PacketCodec<RegistryByteBuf, MetatagSyncS2CPayload<?, ?>> CODEC = Identifier.PACKET_CODEC.<Registry<?>>xmap(
			Registries.ROOT::get,
			registry -> registry.getKey().getValue()
		).<RegistryByteBuf>cast()
		.<Metatag<?, ?>>dispatch(
			Metatag::registry,
			registry -> Identifier.PACKET_CODEC.xmap(
				id -> MetatagHolder.of(registry).specter$getMetatag(id),
				Metatag::id
			)
		)
		.dispatch(
			MetatagSyncS2CPayload::metatag,
			MetatagSyncS2CPayload::createCodec
		);
	private static @Nullable List<MetatagSyncS2CPayload<?, ?>> CACHE = null;

	private static <R, V> PacketCodec<RegistryByteBuf, MetatagSyncS2CPayload<R, V>> createCodec(Metatag<R, V> metatag) {
		return SpecterPacketCodecs.pair(
			PacketCodecs.registryValue(metatag.registry().getKey()),
			metatag.packetCodec()
		).collect(PacketCodecs.toList()).xmap(
			list -> new MetatagSyncS2CPayload<>(metatag, list),
			MetatagSyncS2CPayload::values
		);
	}

	private static <R, V> MetatagSyncS2CPayload<R, V> createPayload(Metatag<R, V> metatag) {
		List<Pair<R, V>> values = new ArrayList<>();
		for (Metatag.Entry<R, V> entry : metatag)
			values.add(Pair.of(
				entry.key(),
				entry.value()
			));
		return new MetatagSyncS2CPayload<>(metatag, List.copyOf(values));
	}

	public static List<MetatagSyncS2CPayload<?, ?>> getOrCreatePayloads() {
		if (CACHE != null) return CACHE;

		List<MetatagSyncS2CPayload<?, ?>> newCache = new ArrayList<>();
		for (Registry<?> registry : Registries.ROOT) {
			MetatagHolder.of(registry).specter$getMetatags().forEach(entry -> {
				if (entry.getValue().side() != ResourceType.SERVER_DATA) return;

				SpecterGlobals.debug("Caching metatag %s".formatted(entry.getKey()));
				newCache.add(createPayload(entry.getValue()));
			});
		}

		CACHE = List.copyOf(newCache);
		return CACHE;
	}

	public static void clearCache() {
		CACHE = null;
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
