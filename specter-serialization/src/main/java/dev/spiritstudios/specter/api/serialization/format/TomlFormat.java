package dev.spiritstudios.specter.api.serialization.format;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import dev.spiritstudios.specter.api.serialization.nightconfig.NightConfigElement;
import dev.spiritstudios.specter.api.serialization.nightconfig.NightConfigMap;
import dev.spiritstudios.specter.api.serialization.nightconfig.NightConfigOps;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;

public class TomlFormat extends NightConfigOps implements DynamicFormat<NightConfigElement> {
	public static final TomlFormat INSTANCE = new TomlFormat();


	@Override
	public void write(Writer writer, NightConfigElement value) throws IOException {
		if (!(value instanceof NightConfigMap object))
			throw new IllegalArgumentException("Cannot write non object to toml file.");

		TomlWriter tomlWriter = new TomlWriter();
		tomlWriter.write(object.config(), writer);
		writer.flush();
	}

	@Override
	public NightConfigElement read(Reader reader) {
		TomlParser parser = new TomlParser();
		CommentedConfig config = parser.parse(reader);
		return new NightConfigMap(config, Collections.emptyList());
	}

	@Override
	public String name() {
		return "toml";
	}
}
