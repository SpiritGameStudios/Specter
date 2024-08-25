package dev.spiritstudios.specter.api.config;

import dev.spiritstudios.specter.api.config.annotations.Comment;
import dev.spiritstudios.specter.api.config.annotations.Range;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A configuration file that can be saved to disk.
 * Implement this interface with static fields for configuration options.
 */
public interface Config {
	/**
	 * WARNING: Recursive method
	 */
	private static void getNestedClasses(List<Class<?>> nestedClasses, Class<?> clazz) {
		nestedClasses.add(clazz);
		for (Class<?> nestedClass : clazz.getDeclaredClasses())
			getNestedClasses(nestedClasses, nestedClass);
	}

	Identifier getId();

	/**
	 * Saves the config to disk.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	default void save() {
		String json = ConfigManager.GSON.toJson(this);
		List<Class<?>> nestedClasses = new ArrayList<>();
		getNestedClasses(nestedClasses, this.getClass());

		Map<String, String> comments = new Object2ObjectOpenHashMap<>();
		for (Field field : this.getClass().getDeclaredFields()) checkAnnotations(comments, field);
		for (Class<?> nestedClass : nestedClasses)
			for (Field field : nestedClass.getDeclaredFields()) checkAnnotations(comments, field);

		List<String> newLines = new ArrayList<>();
		for (String line : json.split("\n")) {
			if (!line.trim().startsWith("\"")) {
				newLines.add(line);
				continue;
			}

			String key = line.split("\"")[1];
			String comment = comments.get(key);
			if (comment == null) {
				newLines.add(line);
				continue;
			}

			String whitespaces = line.substring(0, line.indexOf("\""));
			comment = whitespaces + "// " + (comment.contains("\n") ? String.join("\n" + whitespaces + "// ", comment.split("\n")) : comment);

			newLines.add(comment);
			newLines.add(line);
		}

		Path path = this.getPath();
		path.toFile().getParentFile().mkdirs();

		try {
			Files.write(path, newLines);
		} catch (IOException e) {
			SpecterGlobals.LOGGER.error("Failed to save config file: {}", path, e);
		}
	}

	default Path getPath() {
		return Paths.get(
			FabricLoader.getInstance().getConfigDir().toString(),
			"",
			String.format("%s.%s", getId().getPath(), "json")
		);
	}

	private void checkAnnotations(Map<String, String> comments, Field field) {
		if (field.isAnnotationPresent(Range.class)) {
			Range annotation = field.getAnnotation(Range.class);
			if (annotation.clamp()) {
				Number value = ReflectionHelper.getFieldValue(this, field);
				if (value != null)
					ReflectionHelper.setFieldValue(
						this,
						field,
						MathHelper.clamp(
							value.doubleValue(),
							annotation.min(),
							annotation.max()
						)
					);
			}
		}

		if (!field.isAnnotationPresent(Comment.class)) return;
		Comment annotation = field.getAnnotation(Comment.class);
		comments.put(field.getName(), annotation.value());
	}
}
