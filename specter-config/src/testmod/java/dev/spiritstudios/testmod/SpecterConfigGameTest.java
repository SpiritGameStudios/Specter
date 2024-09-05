package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.config.ConfigManager;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public final class SpecterConfigGameTest {
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testCreateConfigFile(TestContext context) throws IOException {
		Path path = Paths.get(
			FabricLoader.getInstance().getConfigDir().toString(),
			"",
			"createtestconfig.json"
		);

		Files.deleteIfExists(path);
		CreateTestConfig config = ConfigManager.getConfig(CreateTestConfig.class);

		context.assertTrue(Files.exists(path), "Config file does not exist");
		context.assertTrue(config.testString.equals("test"), "String is not equal to test, Make sure you haven't modified the config");
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testSaveConfigFile(TestContext context) throws IOException {
		Path path = Paths.get(
			FabricLoader.getInstance().getConfigDir().toString(),
			"",
			"gettestconfig.json"
		);

		Files.deleteIfExists(path);
		GetTestConfig config = ConfigManager.getConfig(GetTestConfig.class);
		GetTestConfig.testString.set("test2");
		config.save();

		GetTestConfig newConfig = ConfigManager.getConfig(GetTestConfig.class);

		context.assertTrue(Files.exists(path), "Config file does not exist");
		context.assertTrue(newConfig.testString.equals("test2"), "String is not equal to test2, Make sure you haven't modified the config");
		context.complete();
	}
}
