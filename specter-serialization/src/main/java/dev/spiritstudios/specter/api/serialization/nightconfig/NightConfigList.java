package dev.spiritstudios.specter.api.serialization.nightconfig;

import java.util.ArrayList;
import java.util.List;

public class NightConfigList extends NightConfigElement {
	private final List<Object> list;

	public NightConfigList(NightConfigList other) {
		super(new ArrayList<>(other.comments()));
		list = new ArrayList<>(other.list);
	}

	public NightConfigList(List<Object> list, List<String> comments) {
		super(comments);
		this.list = list;
	}

	public void add(NightConfigElement element) {
		list.add(element.toObject());
	}

	public void addAll(NightConfigList elements) {
		this.list.addAll(elements.list);
	}

	public void addAll(List<NightConfigElement> elements) {
		this.list.addAll(elements.stream().map(NightConfigElement::toObject).toList());
	}

	@Override
	public Object toObject() {
		return list;
	}

	public List<NightConfigElement> toElements() {
		return list.stream()
			.map(NightConfigElement::ofObject)
			.toList();
	}
}
