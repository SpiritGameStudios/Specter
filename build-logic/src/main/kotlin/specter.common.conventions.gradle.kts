import org.gradle.accessors.dm.LibrariesForLibs
import java.net.URI

plugins {
	`maven-publish`
	`java-library`
	checkstyle
	id("com.diffplug.spotless")
	id("fabric-loom")
}

group = "dev.spiritstudios.specter"
version = rootProject.property("mod.version").toString()

val libs = the<LibrariesForLibs>()

dependencies {
	minecraft(libs.minecraft)
	@Suppress("UnstableApiUsage")
	mappings(
		loom.layered {
			officialMojangMappings()
			parchment(libs.parchment)
		}
	)
	modImplementation(libs.fabric.loader)

	modImplementation(libs.fabric.api)
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.release = 21
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

spotless {
	lineEndings = com.diffplug.spotless.LineEnding.UNIX

	java {
		removeUnusedImports()
		leadingSpacesToTabs()
		trimTrailingWhitespace()
	}

	json {
		target("src/**/lang/*.json")
		targetExclude("src/**/generated/**")
		gson().indentWithSpaces(4).sortByKeys()
	}
}

checkstyle {
	configFile = rootProject.file("checkstyle.xml")
}

tasks.withType<ProcessResources> {
	val map = mapOf(
		"mod_version" to rootProject.property("mod.version").toString()
	)

	inputs.properties(map)
	filesMatching("fabric.mod.json") { expand(map) }
}

loom {
	splitEnvironmentSourceSets()

	if (file("src/main/resources/specter-${name}.accesswidener").exists()) {
		accessWidenerPath = file("src/main/resources/specter-${name}.accesswidener")
	}
}

extensions.configure(PublishingExtension::class.java) {
	repositories {
		maven {
			name = "SpiritStudiosReleases"
			url = URI("https://maven.spiritstudios.dev/releases")
			credentials(PasswordCredentials::class)
		}

		maven {
			name = "SpiritStudiosSnapshots"
			url = URI("https://maven.spiritstudios.dev/snapshots")
			credentials(PasswordCredentials::class)
		}

		mavenLocal()
	}
}
