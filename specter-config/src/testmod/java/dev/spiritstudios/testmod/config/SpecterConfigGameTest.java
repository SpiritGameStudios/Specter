package dev.spiritstudios.testmod.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.loader.api.FabricLoader;

import dev.spiritstudios.specter.api.config.ConfigHolder;

@SuppressWarnings("unused")
public final class SpecterConfigGameTest {
	private void testConfig(TestContext context, Path path, ConfigHolder<TestConfig, ?> holder) throws IOException {
		Files.deleteIfExists(path);
		holder.save();

		TestConfig config = holder.get();

		context.assertTrue(holder.load(), Text.of("Config file failed to load"));
		context.assertTrue(Files.exists(path), Text.of("Config file does not exist"));

		context.assertFalse(
				config.testString.checkConstraints("meow").isSuccess(),
				Text.of("Constraint on value testString did not flag \"meow\" as invalid")
		);

		context.assertTrue(config.testString.get().equals("test@example.com"), Text.of("String is not equal to test, Make sure you haven't modified the config"));
		context.assertTrue(config.nestedConfig.nestedString.get().equals("nested"), Text.of("String is not equal to nested, Make sure you haven't modified the config"));
		context.assertTrue(config.nestedConfig.nestedNestedConfig.nestedNestedString.get().equals("nestednested"), Text.of("String is not equal to nestednested, Make sure you haven't modified the config"));

		config.testString.set("test2@example.com");
		holder.save();

		context.assertTrue(holder.load(), Text.of("Config file failed to load"));
		context.assertTrue(Files.exists(path), Text.of("Config file does not exist"));

		context.assertTrue(
				config.testString.get().equals("test2@example.com"),
				Text.of("String is not equal to test2@example.com, Make sure you haven't modified the config")
		);

		Files.deleteIfExists(path);

		context.complete();
	}

	@GameTest
	public void testTomlConfig(TestContext context) throws IOException {
		testConfig(context, Paths.get(
				FabricLoader.getInstance().getConfigDir().toString(),
				"",
				"tomltestconfig.toml"
		), TestConfig.TOML_HOLDER);
	}

	@GameTest
	public void testJsonCConfig(TestContext context) throws IOException {
		testConfig(context, Paths.get(
				FabricLoader.getInstance().getConfigDir().toString(),
				"",
				"jsontestconfig.json"
		), TestConfig.JSON_HOLDER);
	}
}
