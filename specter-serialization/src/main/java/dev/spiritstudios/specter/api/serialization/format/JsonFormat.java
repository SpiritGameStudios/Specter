package dev.spiritstudios.specter.api.serialization.format;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.Strictness;
import com.mojang.serialization.JsonOps;

public final class JsonFormat {
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.setStrictness(Strictness.LENIENT)
			.create();
	public static final DynamicFormat<JsonElement> INSTANCE = DynamicFormat.of(
			JsonOps.INSTANCE,
			(writer, value) -> GSON.toJson(value, JsonElement.class, writer),
			reader -> GSON.fromJson(reader, JsonElement.class),
			"json"
	);
	private static final Gson COMPRESSED_GSON = new GsonBuilder()
			.setStrictness(Strictness.LENIENT)
			.create();
	public static final DynamicFormat<JsonElement> COMPRESSED = DynamicFormat.of(
			JsonOps.COMPRESSED,
			(writer, value) -> COMPRESSED_GSON.toJson(value, JsonElement.class, writer),
			reader -> COMPRESSED_GSON.fromJson(reader, JsonElement.class),
			"json"
	);
}
