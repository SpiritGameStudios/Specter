pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		gradlePluginPortal()
	}

	includeBuild("build-logic")
}

rootProject.name = "specter"

include("core")

include("dfu")
include("render")
include("config")
include("registry")
include("item")
include("block")
include("entity")
include("debug")
include("gui")
include("serialization")
include("worldgen")
