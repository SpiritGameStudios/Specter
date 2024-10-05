package dev.spiritstudios.testmod;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class SpecterEntityGameTest {
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testDefaultAttributes(TestContext context) {
		WardenEntity warden = context.spawnEntity(EntityType.WARDEN, 0, 0, 0);
		warden.damage(context.getWorld().getDamageSources().generic(), 1);

		context.assertTrue(warden.isDead(), "Warden should be dead after being attacked by player");
		context.complete();
	}
}
