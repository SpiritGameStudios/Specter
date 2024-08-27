package dev.spiritstudios.specter.mixin.item.client;

import dev.spiritstudios.specter.impl.item.ItemGroupReloader;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {
	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo ci) {
		if (!ItemGroupReloader.RELOADED) return;
		((FabricCreativeInventoryScreen) this).switchToPage(0);
		ItemGroupReloader.RELOADED = false;
	}
}
