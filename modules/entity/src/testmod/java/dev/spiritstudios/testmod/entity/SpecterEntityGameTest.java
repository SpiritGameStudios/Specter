package dev.spiritstudios.testmod.entity;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.warden.Warden;

public class SpecterEntityGameTest {
	@GameTest
	public void testDefaultAttributes(GameTestHelper context) {
		Warden warden = context.spawn(EntityType.WARDEN, 0, 0, 0);
		warden.hurtServer(context.getLevel(), context.getLevel().damageSources().generic(), 1);

		context.assertTrue(warden.isDeadOrDying(), Component.nullToEmpty("Warden should be dead after being attacked by player"));
		context.succeed();
	}
}
