pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		gradlePluginPortal()
	}
}

rootProject.name = "specter-api"

include("specter-core")
include("specter-dfu")
include("specter-render")
include("specter-config")
include("specter-registry")
include("specter-item")
include("specter-block")
include("specter-entity")
include("specter-debug")
include("specter-gui")
include("specter-serialization")
