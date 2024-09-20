package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.Value;
import net.minecraft.util.Identifier;

public class GetTestConfig extends Config<GetTestConfig> {
	@Override
	public Identifier getId() {
		return Identifier.of("specter-config-testmod", "gettestconfig");
	}

	public static final GetTestConfig INSTANCE = Config.create(GetTestConfig.class);

	public String invalidField = "test";

	public Value<String> testString = stringValue("test")
		.comment("This is a test string")
		.sync()
		.build();

	public Value<Integer> testInt = intValue(2)
		.comment("This is a test int")
		.range(2, 10)
		.step(3)
		.build();

	public Value<Boolean> testBool = booleanValue(true)
		.comment("This is a test bool")
		.build();

	public Value<Double> testDouble = doubleValue(1.0)
		.comment("This is a test double")
		.range(0.0, 10.0)
		.step(0.05)
		.build();

	public Value<Float> testFloat = floatValue(1.0f)
		.comment("This is a test float")
		.range(0.0f, 5.0f)
		.step(0.5)
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
