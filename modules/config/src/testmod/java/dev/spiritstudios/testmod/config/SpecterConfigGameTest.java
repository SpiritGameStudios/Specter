package dev.spiritstudios.testmod.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
public final class SpecterConfigGameTest {
	@GameTest
	public void testTomlConfig(GameTestHelper context) throws IOException {
		Path path = Paths.get(
				FabricLoader.getInstance().getConfigDir().toString(),
				"",
				"tomltestconfig.toml"
		);

		Files.deleteIfExists(path);
		TestConfig.TOML_HOLDER.save();
		context.assertTrue(TestConfig.TOML_HOLDER.load(), Component.nullToEmpty("Config file failed to load"));
		context.assertTrue(Files.exists(path), Component.nullToEmpty("Config file does not exist"));

		context.assertTrue(TestConfig.TOML_HOLDER.get().testString.get().equals("test"), Component.nullToEmpty("String is not equal to test, Make sure you haven't modified the config"));
		context.assertTrue(TestConfig.TOML_HOLDER.get().nestedConfig.get().nestedString.get().equals("nested"), Component.nullToEmpty("String is not equal to nested, Make sure you haven't modified the config"));
		context.assertTrue(TestConfig.TOML_HOLDER.get().nestedConfig.get().nestedNestedConfig.get().nestedNestedString.get().equals("nestednested"), Component.nullToEmpty("String is not equal to nestednested, Make sure you haven't modified the config"));

		TestConfig.TOML_HOLDER.get().testString.set("test2");
		TestConfig.TOML_HOLDER.save();
		context.assertTrue(TestConfig.TOML_HOLDER.load(), Component.nullToEmpty("Config file failed to load"));
		context.assertTrue(Files.exists(path), Component.nullToEmpty("Config file does not exist"));
		context.assertTrue(TestConfig.TOML_HOLDER.get().testString.get().equals("test2"), Component.nullToEmpty("String is not equal to test2, Make sure you haven't modified the config"));
		Files.deleteIfExists(path);

		context.succeed();
	}

	@GameTest
	public void testJsonCConfig(GameTestHelper context) throws IOException {
		Path path = Paths.get(
				FabricLoader.getInstance().getConfigDir().toString(),
				"",
				"jsontestconfig.json"
		);

		Files.deleteIfExists(path);
		context.assertTrue(TestConfig.JSON_HOLDER.load(), Component.nullToEmpty("Config file failed to load"));
		context.assertTrue(Files.exists(path), Component.nullToEmpty("Config file does not exist"));

		context.assertTrue(TestConfig.JSON_HOLDER.get().testString.get().equals("test"), Component.nullToEmpty("String is not equal to test, Make sure you haven't modified the config"));
		context.assertTrue(TestConfig.JSON_HOLDER.get().nestedConfig.get().nestedString.get().equals("nested"), Component.nullToEmpty("String is not equal to nested, Make sure you haven't modified the config"));
		context.assertTrue(TestConfig.JSON_HOLDER.get().nestedConfig.get().nestedNestedConfig.get().nestedNestedString.get().equals("nestednested"), Component.nullToEmpty("String is not equal to nestednested, Make sure you haven't modified the config"));

		TestConfig.JSON_HOLDER.get().testString.set("test2");
		TestConfig.JSON_HOLDER.save();
		context.assertTrue(TestConfig.JSON_HOLDER.load(), Component.nullToEmpty("Config file failed to load"));
		context.assertTrue(Files.exists(path), Component.nullToEmpty("Config file does not exist"));
		context.assertTrue(TestConfig.JSON_HOLDER.get().testString.get().equals("test2"), Component.nullToEmpty("String is not equal to test2, Make sure you haven't modified the config"));
		Files.deleteIfExists(path);

		context.succeed();
	}
}
