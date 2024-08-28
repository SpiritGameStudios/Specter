package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.NestedConfig;
import dev.spiritstudios.specter.api.config.annotations.Comment;
import dev.spiritstudios.specter.api.config.annotations.Range;
import dev.spiritstudios.specter.api.config.annotations.Sync;
import net.minecraft.util.Identifier;

public class CreateTestConfig implements Config {
	@Override
	public Identifier getId() {
		return Identifier.of("specter-config-testmod", "createtestconfig");
	}

	@Comment("This is a test string")
	@Sync
	public String testString = "test";
	@Comment("This is a test int")
	@Range(min = 2, max = 10)
	public int testInt = 1;
	@Comment("This is a test bool")
	public boolean testBool = true;
	@Comment("This is a test double")
	public double testDouble = 1.0;
	@Comment("This is a test float")
	public float testFloat = 1.0f;

	@Comment("This is a nested class")
	public Nested nested = new Nested();

	@Comment("This is a test enum")
	public TestEnum testEnum = TestEnum.TEST_1;

	public static class Nested implements NestedConfig {
		@Comment("This is a nested string\n" +
			"With a new line")
		public String nestedString = "test";

		@Override
		public Identifier getId() {
			return Identifier.of("testmod", "nested");
		}

		@Comment("This is a nested nested class")
		public NestedNested nestedNested = new NestedNested();

		public static class NestedNested implements NestedConfig {
			@Comment("This is a nested nested string")
			public String nestedNestedString = "test";

			@Override
			public Identifier getId() {
				return Identifier.of("testmod", "nestednested");
			}
		}
	}

	public enum TestEnum {
		TEST_1,
		TEST_2,
		TEST_3
	}
}
