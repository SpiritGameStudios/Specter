package dev.spiritstudios.testmod.config;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.api.config.Constraint;
import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.config.gui.GuiHint;
import dev.spiritstudios.specter.api.serialization.format.JsonCFormat;
import dev.spiritstudios.specter.api.serialization.format.TomlFormat;

public class TestConfig extends Config {
	public static final ConfigHolder<TestConfig, ?> JSON_HOLDER = ConfigHolder
			.builder(Identifier.of("specter-config-testmod", "jsontestconfig"), TestConfig.class)
			.format(JsonCFormat.INSTANCE)
			.build();

	public static final ConfigHolder<TestConfig, ?> TOML_HOLDER = ConfigHolder
			.builder(Identifier.of("specter-config-testmod", "tomltestconfig"), TestConfig.class)
			.format(TomlFormat.INSTANCE)
			.build();

	public final String invalidField = "test";

	public final Value<String> testString = stringValue("test")
			.comment("This is a test string")
			.sync()
			.build();

	public final Value<Integer> testInt = intValue(2)
			.comment("This is a test int")
			.constrain(Constraint.range(2, 10))
			.guiHint(GuiHint.slider(3))
			.sync()
			.build();

	public final Value<Boolean> testBool = booleanValue(true)
			.comment("This is a test bool")
			.build();

	public final Value<Double> testDouble = doubleValue(1.0)
			.comment("This is a test double")
			.constrain(Constraint.range(0.0, 10.0))
//			.step(0.05)
			.build();

	public final Value<Float> testFloat = floatValue(1.0f)
			.comment("This is a test float")
			.constrain(Constraint.range(0.0f, 5.0f))
//			.step(0.5)
			.build();

	public final Value<TestEnum> testEnum = enumValue(TestEnum.TEST_1, TestEnum.class)
			.comment("This is a test enum")
			.build();

	public final Value<Item> testItem = registryValue(Items.BEDROCK, Registries.ITEM)
			.comment("This is a test item")
			.build();

	public final SubTestConfig nestedConfig = new SubTestConfig();

	public enum TestEnum {
		TEST_1,
		TEST_2,
		TEST_3
	}

	public static class SubTestConfig extends SubConfig {
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

		public final NestedSubTestConfig nestedNestedConfig = new NestedSubTestConfig();

		public SubTestConfig() {
			super(null);
		}

		public static class NestedSubTestConfig extends SubConfig {
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

			public NestedSubTestConfig() {
				super(null);
			}
		}
	}
}
