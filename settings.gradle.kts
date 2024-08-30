pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		mavenCentral()
		gradlePluginPortal()
	}
}

rootProject.name = "specter-api"

include("specter-core")
include("specter-render")
include("specter-config")
include("specter-registry")
include("specter-item")
include("specter-block")
include("specter-debug")
