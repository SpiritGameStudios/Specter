package dev.spiritstudios.specter.impl.config.network;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.ConfigManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static dev.spiritstudios.specter.api.core.SpecterGlobals.MODID;

public record ConfigSyncS2CPayload(Config<?> config) implements CustomPayload {
    public static final Id<ConfigSyncS2CPayload> ID = new Id<>(Identifier.of(MODID, "config_sync"));
    public static final PacketCodec<ByteBuf, ConfigSyncS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodec.of(
                    Config::packetEncode,
                    buf -> {
                        Identifier id = Identifier.PACKET_CODEC.decode(buf);
                        return ConfigManager.getConfigById(id).packetDecode(buf);
                    }
            ),
            ConfigSyncS2CPayload::config,
            ConfigSyncS2CPayload::new
    );

    private static final List<ConfigSyncS2CPayload> CACHE = new ArrayList<>();

    public static void clearCache() {
        CACHE.clear();
    }

    public static List<ConfigSyncS2CPayload> createPayloads() {
        if (CACHE.isEmpty()) {
            CACHE.addAll(
                    ConfigManager.getConfigs().values().stream()
                            .map(ConfigSyncS2CPayload::new)
                            .toList()
            );
        }


        return CACHE;
    }

    @Override
    public Id<ConfigSyncS2CPayload> getId() {
        return ID;
    }
}
