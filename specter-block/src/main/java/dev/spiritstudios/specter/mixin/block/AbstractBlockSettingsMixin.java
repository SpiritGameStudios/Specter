package dev.spiritstudios.specter.mixin.block;

import dev.spiritstudios.specter.api.block.SpecterBlock;
import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractBlock.Settings.class)
public class AbstractBlockSettingsMixin implements SpecterBlock.Settings {
}
