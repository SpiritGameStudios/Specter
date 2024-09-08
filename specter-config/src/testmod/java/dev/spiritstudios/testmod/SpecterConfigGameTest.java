package dev.spiritstudios.testmod;

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
		context.assertTrue(CreateTestConfig.INSTANCE.load(), "Config file failed to load");
		context.assertTrue(Files.exists(path), "Config file does not exist");
		context.assertTrue(CreateTestConfig.INSTANCE.testString.get().equals("test"), "String is not equal to test, Make sure you haven't modified the config");
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

		context.assertTrue(GetTestConfig.INSTANCE.load(), "Config file failed to load");
		GetTestConfig.INSTANCE.testString.set("test2");
		GetTestConfig.INSTANCE.save();
		context.assertTrue(GetTestConfig.INSTANCE.load(), "Config file failed to load");

		context.assertTrue(Files.exists(path), "Config file does not exist");
		context.assertTrue(GetTestConfig.INSTANCE.testString.get().equals("test2"), "String is not equal to test2, Make sure you haven't modified the config");
		context.complete();
	}
}
