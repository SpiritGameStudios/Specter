plugins {
	`kotlin-dsl`
}

repositories {
	maven("https://maven.fabricmc.net/")
	gradlePluginPortal()
	mavenCentral()
}

dependencies {
	implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
	implementation(libs.plugin.spotless)
	implementation(libs.plugin.fabric.loom)
}

