package dev.spiritstudios.specter.api.serialization;

/**
 * Represents an object that can have comments.
 */
public interface Commentable {
	void setComments(String... comments);
	String[] comments();
}
