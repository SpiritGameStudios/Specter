package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.Value;
import net.minecraft.util.Identifier;

public class CreateTestConfig extends Config<CreateTestConfig> {
	@Override
	public Identifier getId() {
		return Identifier.of("specter-config-testmod", "createtestconfig");
	}

	public static final CreateTestConfig INSTANCE = Config.create(CreateTestConfig.class);

	public Value<String> testString = stringValue("test")
		.comment("This is a test string")
		.sync()
		.build();

	public Value<Integer> testInt = intValue(1)
		.comment("This is a test int")
		.range(2, 10)
		.build();

	public Value<Boolean> testBool = booleanValue(true)
		.comment("This is a test bool")
		.build();

	public Value<Double> testDouble = doubleValue(1.0)
		.comment("This is a test double")
		.build();

	public Value<Float> testFloat = floatValue(1.0f)
		.comment("This is a test float")
		.build();

	public Value<TestEnum> testEnum = enumValue(TestEnum.TEST_1, TestEnum.class)
		.comment("This is a test enum")
		.build();


	public enum TestEnum {
		TEST_1,
		TEST_2,
		TEST_3
	}
}
