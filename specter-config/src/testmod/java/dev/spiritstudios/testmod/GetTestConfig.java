package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.config.Config;
import net.minecraft.util.Identifier;

public class GetTestConfig extends Config<GetTestConfig> {
	@Override
	public Identifier getId() {
		return Identifier.of("specter-config-testmod", "gettestconfig");
	}

	public static Value<String> testString = stringValue("test")
		.comment("This is a test string")
		.sync()
		.build();

	public static Value<Integer> testInt = intValue(2)
		.comment("This is a test int")
		.range(2, 10)
		.build();

	public static Value<Boolean> testBool = booleanValue(true)
		.comment("This is a test bool")
		.build();

	public static Value<Double> testDouble = doubleValue(1.0)
		.comment("This is a test double")
		.build();

	public static Value<Float> testFloat = floatValue(1.0f)
		.comment("This is a test float")
		.build();

	public static Value<TestEnum> testEnum = enumValue(TestEnum.TEST_1, TestEnum.class)
		.comment("This is a test enum")
		.build();


	public enum TestEnum {
		TEST_1,
		TEST_2,
		TEST_3
	}
}
