package dev.spiritstudios.specter.mixin.item.client;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {
//	@Inject(method = "init", at = @At("RETURN"))
//	private void init(CallbackInfo ci) {
//		if (!ItemGroupReloader.RELOADED) return;
//
//		((FabricCreativeInventoryScreen) this).switchToPage(0);
//		ItemGroupReloader.RELOADED = false;
//	}
}
