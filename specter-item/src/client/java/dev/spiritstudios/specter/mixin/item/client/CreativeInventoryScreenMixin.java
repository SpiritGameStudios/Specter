package dev.spiritstudios.specter.mixin.item.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;

import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;

import dev.spiritstudios.specter.impl.item.client.SpecterItemClient;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {
	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo ci) {
		if (!SpecterItemClient.justReloaded()) return;
		((FabricCreativeInventoryScreen) this).switchToPage(0);
		SpecterItemClient.reloadDone();
	}
}
