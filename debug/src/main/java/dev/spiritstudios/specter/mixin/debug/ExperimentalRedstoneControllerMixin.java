package dev.spiritstudios.specter.mixin.debug;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.s2c.custom.DebugRedstoneUpdateOrderCustomPayload;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExperimentalRedstoneController;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;

@Mixin(ExperimentalRedstoneController.class)
public abstract class ExperimentalRedstoneControllerMixin {
	@Shadow
	@Final
	private Object2IntMap<BlockPos> wireOrientationsAndPowers;

	@Shadow
	private static WireOrientation unpackOrientation(int packed) {
		return null;
	}

	@Inject(method = "update(Lnet/minecraft/world/World;)V", at = @At("HEAD"))
	private void debugPacket(World world, CallbackInfo ci) {
		DebugInfoSender.sendRedstoneUpdateOrder(world, new DebugRedstoneUpdateOrderCustomPayload(world.getTime(), wireOrientationsAndPowers.object2IntEntrySet().stream().map(entry -> {
			WireOrientation wireOrientation = unpackOrientation(entry.getIntValue());

			return new DebugRedstoneUpdateOrderCustomPayload.Wire(entry.getKey(), wireOrientation);
		}).toList()));
	}
}
