package dev.spiritstudios.specter.mixin.debug;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Inject(method = "exceptionCaught", at = @At("RETURN"))
	private void exceptionCaught(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
		LOGGER.error("Failed to handle packet", ex);
	}
}
