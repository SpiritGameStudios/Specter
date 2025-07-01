package dev.spiritstudios.testmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class SpecterEntityGameTest {
	@GameTest
	public void testDefaultAttributes(TestContext context) {
		WardenEntity warden = context.spawnEntity(EntityType.WARDEN, 0, 0, 0);
		warden.damage(context.getWorld(), context.getWorld().getDamageSources().generic(), 1);

		context.assertTrue(warden.isDead(), Text.of("Warden should be dead after being attacked by player"));
		context.complete();
	}
}
