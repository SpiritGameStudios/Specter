pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		gradlePluginPortal()
	}

	includeBuild("build-logic")
}

rootProject.name = "specter"

fun module(path: String) {
	include(path)
	val project = project(":$path")
	project.projectDir = file("modules/$path")
}

module("core")
module("dfu")
module("render")
module("config")
module("registry")
module("item")
module("block")
module("entity")
module("debug")
module("gui")
module("serialization")
module("worldgen")
