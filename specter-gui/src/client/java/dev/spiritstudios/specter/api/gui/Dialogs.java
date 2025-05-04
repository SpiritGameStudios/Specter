package dev.spiritstudios.specter.api.gui;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

// TODO: finish this
public final class Dialogs {
	public static void notificationPopup(String title, String message, Icon icon) {
		TinyFileDialogs.tinyfd_notifyPopup(title, message, icon.name);
	}

	public enum Icon {
		INFO("info"),
		ERROR("error"),
		WARNING("warning");

		private final String name;

		Icon(String name) {
			this.name = name;
		}
	}
}
