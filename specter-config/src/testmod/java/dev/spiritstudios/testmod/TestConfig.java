package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.serialization.format.JsonCFormat;
import dev.spiritstudios.specter.api.serialization.format.TomlFormat;
import net.minecraft.util.Identifier;

public class TestConfig extends Config<TestConfig> {
	public static final ConfigHolder<TestConfig, ?> JSON_HOLDER = ConfigHolder
		.builder(Identifier.of("specter-config-testmod", "jsontestconfig"), TestConfig.class)
		.format(JsonCFormat.INSTANCE)
		.build();

	public static final ConfigHolder<TestConfig, ?> TOML_HOLDER = ConfigHolder
		.builder(Identifier.of("specter-config-testmod", "tomltestconfig"), TestConfig.class)
		.format(TomlFormat.INSTANCE)
		.build();

	public static final TestConfig JSON = JSON_HOLDER.get();
	public static final TestConfig TOML = TOML_HOLDER.get();

	public final String invalidField = "test";

	public final Value<String> testString = stringValue("test")
		.comment("This is a test string")
		.sync()
		.build();

	public final Value<Integer> testInt = intValue(2)
		.comment("This is a test int")
		.range(2, 10)
		.step(3)
		.build();

	public final Value<Boolean> testBool = booleanValue(true)
		.comment("This is a test bool")
		.build();

	public final Value<Double> testDouble = doubleValue(1.0)
		.comment("This is a test double")
		.range(0.0, 10.0)
		.step(0.05)
		.build();

	public final Value<Float> testFloat = floatValue(1.0f)
		.comment("This is a test float")
		.range(0.0f, 5.0f)
		.step(0.5)
		.build();

	public final Value<TestEnum> testEnum = enumValue(TestEnum.TEST_1, TestEnum.class)
		.comment("This is a test enum")
		.build();

	public final Value<NestedConfig> nestedConfig = nestedValue(NestedConfig.class)
		.comment("This is a nested config")
		.sync()
		.build();

	public static class NestedConfig extends Config<NestedConfig> {
		public final Value<String> nestedString = stringValue("nested")
			.comment("This is a nested string")
			.sync()
			.build();

		public final Value<Integer> nestedInt = intValue(3)
			.comment("This is a nested int")
			.sync()
			.build();

		public final Value<Boolean> nestedBool = booleanValue(false)
			.comment("This is a nested bool")
			.sync()
			.build();

		public final Value<Double> nestedDouble = doubleValue(2.0)
			.comment("This is a nested double")
			.sync()
			.build();

		public final Value<Float> nestedFloat = floatValue(2.0f)
			.comment("This is a nested float")
			.sync()
			.build();

		public final Value<TestEnum> nestedEnum = enumValue(TestEnum.TEST_2, TestEnum.class)
			.comment("This is a nested enum")
			.sync()
			.build();

		public final Value<NestedNestedConfig> nestedNestedConfig = nestedValue(NestedNestedConfig.class)
			.comment("This is a nested nested config")
			.sync()
			.build();

		public static class NestedNestedConfig extends Config<NestedNestedConfig> {
			public final Value<String> nestedNestedString = stringValue("nestednested")
				.comment("This is a nested nested string")
				.sync()
				.build();

			public final Value<Integer> nestedNestedInt = intValue(4)
				.comment("This is a nested nested int")
				.sync()
				.build();

			public final Value<Boolean> nestedNestedBool = booleanValue(true)
				.comment("This is a nested nested bool")
				.sync()
				.build();

			public final Value<Double> nestedNestedDouble = doubleValue(3.0)
				.comment("This is a nested nested double")
				.sync()
				.build();

			public final Value<Float> nestedNestedFloat = floatValue(3.0f)
				.comment("This is a nested nested float")
				.sync()
				.build();

			public final Value<TestEnum> nestedNestedEnum = enumValue(TestEnum.TEST_3, TestEnum.class)
				.comment("This is a nested nested enum")
				.sync()
				.build();
		}
	}

	public enum TestEnum {
		TEST_1,
		TEST_2,
		TEST_3
	}
}
