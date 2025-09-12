package dev.spiritstudios.specter.api.serialization;

import java.util.List;

/**
 * Represents an object that can have comments.
 */
public interface Commentable {
	void setComments(List<String> comments);
	List<String> comments();
}
