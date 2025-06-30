package dev.spiritstudios.testmod.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.loader.api.FabricLoader;

@SuppressWarnings("unused")
public final class SpecterConfigGameTest {
	@GameTest
	public void testTomlConfig(TestContext context) throws IOException {
		Path path = Paths.get(
				FabricLoader.getInstance().getConfigDir().toString(),
				"",
				"tomltestconfig.toml"
		);

		Files.deleteIfExists(path);
		TestConfig.TOML_HOLDER.save();
		context.assertTrue(TestConfig.TOML_HOLDER.load(), Text.of("Config file failed to load"));
		context.assertTrue(Files.exists(path), Text.of("Config file does not exist"));

		context.assertTrue(TestConfig.TOML_HOLDER.get().testString.get().equals("test"), Text.of("String is not equal to test, Make sure you haven't modified the config"));
		context.assertTrue(TestConfig.TOML_HOLDER.get().nestedConfig.nestedString.get().equals("nested"), Text.of("String is not equal to nested, Make sure you haven't modified the config"));
		context.assertTrue(TestConfig.TOML_HOLDER.get().nestedConfig.nestedNestedConfig.nestedNestedString.get().equals("nestednested"), Text.of("String is not equal to nestednested, Make sure you haven't modified the config"));

		TestConfig.TOML_HOLDER.get().testString.set("test2");
		TestConfig.TOML_HOLDER.save();
		context.assertTrue(TestConfig.TOML_HOLDER.load(), Text.of("Config file failed to load"));
		context.assertTrue(Files.exists(path), Text.of("Config file does not exist"));
		context.assertTrue(TestConfig.TOML_HOLDER.get().testString.get().equals("test2"), Text.of("String is not equal to test2, Make sure you haven't modified the config"));
		Files.deleteIfExists(path);

		context.complete();
	}

	@GameTest
	public void testJsonCConfig(TestContext context) throws IOException {
		Path path = Paths.get(
				FabricLoader.getInstance().getConfigDir().toString(),
				"",
				"jsontestconfig.json"
		);

		Files.deleteIfExists(path);
		context.assertTrue(TestConfig.JSON_HOLDER.load(), Text.of("Config file failed to load"));
		context.assertTrue(Files.exists(path), Text.of("Config file does not exist"));

		context.assertTrue(TestConfig.JSON_HOLDER.get().testString.get().equals("test"), Text.of("String is not equal to test, Make sure you haven't modified the config"));
		context.assertTrue(TestConfig.JSON_HOLDER.get().nestedConfig.nestedString.get().equals("nested"), Text.of("String is not equal to nested, Make sure you haven't modified the config"));
		context.assertTrue(TestConfig.JSON_HOLDER.get().nestedConfig.nestedNestedConfig.nestedNestedString.get().equals("nestednested"), Text.of("String is not equal to nestednested, Make sure you haven't modified the config"));

		TestConfig.JSON_HOLDER.get().testString.set("test2");
		TestConfig.JSON_HOLDER.save();
		context.assertTrue(TestConfig.JSON_HOLDER.load(), Text.of("Config file failed to load"));
		context.assertTrue(Files.exists(path), Text.of("Config file does not exist"));
		context.assertTrue(TestConfig.JSON_HOLDER.get().testString.get().equals("test2"), Text.of("String is not equal to test2, Make sure you haven't modified the config"));
		Files.deleteIfExists(path);

		context.complete();
	}
}
