package dev.spiritstudios.specter.api.serialization.toml;

import com.google.common.collect.ImmutableMap;
import org.tomlj.TomlPosition;
import org.tomlj.TomlTable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TomlTableElement extends TomlElement implements TomlTable {
	private final Map<String, TomlElement> members;

	public TomlTableElement(String... comments) {
		super(comments);
		this.members = new LinkedHashMap<>();
	}

	public TomlTableElement(TomlTable table, String... comments) {
		super(comments);
		this.members = table.toMap().entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> TomlElement.of(entry.getValue())));
	}

	public void put(String key, TomlElement value) {
		members.put(key, value);
	}

	public void remove(String key) {
		members.remove(key);
	}

	@Override
	public int size() {
		return members.size();
	}

	@Override
	public boolean isEmpty() {
		return members.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return members.keySet();
	}

	@Override
	public Set<List<String>> keyPathSet(boolean includeTables) {
		return members.entrySet().stream().flatMap(entry -> {
			String key = entry.getKey();
			List<String> basePath = Collections.singletonList(key);

			TomlElement element = entry.getValue();
			if (!(element instanceof TomlTable table)) return Stream.of(basePath);

			Stream<List<String>> subKeys = table.keyPathSet(includeTables).stream().map(subPath -> {
				List<String> path = new ArrayList<>(subPath.size() + 1);
				path.add(key);
				path.addAll(subPath);
				return path;
			});

			if (includeTables) return Stream.concat(Stream.of(basePath), subKeys);
			else return subKeys;
		}).collect(Collectors.toSet());
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		return members.entrySet()
			.stream()
			.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().to()))
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public Set<Map.Entry<List<String>, Object>> entryPathSet(boolean includeTables) {
		return members.entrySet().stream().flatMap(entry -> {
			String key = entry.getKey();
			List<String> entryPath = Collections.singletonList(key);
			TomlElement element = entry.getValue();

			if (!(element instanceof TomlTable table)) return Stream.of(new AbstractMap.SimpleEntry<>(entryPath, element.to()));

			Stream<Map.Entry<List<String>, Object>> subEntries =
				table.entryPathSet(includeTables).stream().map(subEntry -> {
					List<String> subPath = subEntry.getKey();
					List<String> path = new ArrayList<>(subPath.size() + 1);
					path.add(key);
					path.addAll(subPath);
					return new AbstractMap.SimpleEntry<>(path, subEntry.getValue());
				});

			if (includeTables)
				return Stream.concat(Stream.of(new AbstractMap.SimpleEntry<>(entryPath, element.to())), subEntries);
			else return subEntries;
		}).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public Object get(List<String> path) {
		if (path.isEmpty()) return this;
		TomlElement element = getEntry(path);
		return element == null ? null : element.to();
	}

	@Override
	public TomlPosition inputPositionOf(List<String> path) {
		if (path.isEmpty()) return TomlPosition.positionAt(1, 1);
		TomlElement element = getEntry(path);
		return element == null ? null : element.position();
	}

	private TomlElement getEntry(List<String> path) {
		TomlTableElement table = this;
		int depth = path.size();
		if (depth == 0) throw new IllegalArgumentException("Path must not be empty");

		for (int i = 0; i < depth - 1; ++i) {
			TomlElement element = table.members.get(path.get(i));
			if (element == null) return null;
			if (element instanceof TomlTableElement subTable) {
				table = subTable;
				continue;
			}
			return null;
		}

		return table.members.get(path.get(depth - 1));
	}

	@Override
	public Map<String, Object> toMap() {
		return members.entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().to()));
	}

	public Map<String, TomlElement> members() {
		return ImmutableMap.copyOf(members);
	}
}
