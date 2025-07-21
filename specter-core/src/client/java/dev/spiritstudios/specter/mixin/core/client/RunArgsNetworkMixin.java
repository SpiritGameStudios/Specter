package dev.spiritstudios.specter.mixin.core.client;

import java.net.Proxy;
import java.util.UUID;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UndashedUuid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.RunArgs;
import net.minecraft.client.session.Session;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.core.Specter;

@Mixin(RunArgs.Network.class)
public class RunArgsNetworkMixin {
	@Shadow
	@Final
	@Mutable
	public Session session;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(Session session, PropertyMap userProperties, PropertyMap profileProperties, Proxy proxy, CallbackInfo ci) {
		if (!SpecterGlobals.DEBUG) return;

		if (!System.getProperties().containsKey("specter.development.username") || !System.getProperties().containsKey("specter.development.uuid")) {
			Specter.LOGGER.info("Development account not set, skipping...");
			return;
		}

		String username = System.getProperty("specter.development.username");
		UUID uuid = UndashedUuid.fromString(System.getProperty("specter.development.uuid").replace("-", ""));

		Specter.LOGGER.info("Using development account {} ({})", username, uuid);
		this.session = new Session(
				username,
				uuid,
				session.getAccessToken(),
				session.getXuid(),
				session.getClientId(),
				session.getAccountType()
		);
	}

}
