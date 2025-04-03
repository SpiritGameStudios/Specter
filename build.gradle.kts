import net.fabricmc.loom.task.RemapJarTask
import java.net.URI

plugins {
	`java-library`
	`maven-publish`
	alias(libs.plugins.fabric.loom)
}

class ModInfo {
	val id = property("mod.id").toString()
	val group = property("mod.group").toString()
	val version = property("mod.version").toString()
}

allprojects {
	val mod = ModInfo()

	group = mod.group
	version = mod.version

	apply(plugin = "maven-publish")

	tasks.withType<GenerateModuleMetadata>().configureEach {
		enabled = false
	}

	apply(plugin = "java-library")
	apply(plugin = "fabric-loom")

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release = 21
	}

	java {
		withSourcesJar()

		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}

	loom {
		splitEnvironmentSourceSets()
		if (file("src/main/resources/${project.name}.accesswidener").exists()) accessWidenerPath =
			file("src/main/resources/${project.name}.accesswidener")
	}

	sourceSets {
		create("testmod") {
			compileClasspath += sourceSets.main.get().compileClasspath
			runtimeClasspath += sourceSets.main.get().runtimeClasspath
		}

		create("testmodClient") {
			compileClasspath += sourceSets.main.get().compileClasspath
			runtimeClasspath += sourceSets.main.get().runtimeClasspath

			compileClasspath += sourceSets.getByName("client").compileClasspath
			runtimeClasspath += sourceSets.getByName("client").runtimeClasspath

			compileClasspath += sourceSets.getByName("testmod").compileClasspath
			runtimeClasspath += sourceSets.getByName("testmod").runtimeClasspath
		}
	}

	loom.runs {
		create("testmodClient") {
			client()
			ideConfigGenerated(project.rootProject == project)
			name = "Testmod Client"
			source(sourceSets["testmodClient"])
		}

		create("testmodServer") {
			server()
			ideConfigGenerated(project.rootProject == project)
			name = "Testmod Server"
			source(sourceSets["testmod"])
		}
	}

	allprojects.forEach {
		loom.mods.register(it.name) {
			sourceSet(it.sourceSets.main.get())
			sourceSet(it.sourceSets["client"])
		}

		loom.mods.register("${it.name}-testmod") {
			sourceSet(it.sourceSets["testmod"])
			sourceSet(it.sourceSets["testmodClient"])
		}
	}

	dependencies {
		minecraft(rootProject.libs.minecraft)
		mappings(variantOf(rootProject.libs.yarn) { classifier("v2") })
		modImplementation(rootProject.libs.fabric.loader)

		modImplementation(rootProject.libs.fabric.api)

		"testmodImplementation"(sourceSets.main.get().output)
		"testmodClientImplementation"(sourceSets.main.get().output)
		"testmodClientImplementation"(sourceSets["client"].output)
		"testmodClientImplementation"(sourceSets["testmod"].output)
	}

	tasks.withType<ProcessResources> {
		val map = mapOf(
			"mod_version" to mod.version,
			"fabric_loader_version" to rootProject.libs.versions.fabric.loader.get(),
			"minecraft_version" to rootProject.libs.versions.minecraft.get()
		)

		inputs.properties(map)
		filesMatching("fabric.mod.json") { expand(map) }
	}
}

tasks.javadoc {
	with(options as StandardJavadocDocletOptions) {
		source = "21"
		encoding = "UTF-8"
		charset("UTF-8")
		memberLevel = JavadocMemberLevel.PACKAGE
		addStringOption("Xdoclint:none", "-quiet")
		links(
			"https://maven.fabricmc.net/docs/yarn-${rootProject.libs.versions.yarn.get()}/"
		)
	}

	allprojects.forEach { proj ->
		source(proj.sourceSets.main.map { it.allJava.srcDirs })
		source(proj.sourceSets.getByName("client").allSource.srcDirs)
	}
	classpath =
		project.files(sourceSets.main.map { it.compileClasspath }, sourceSets.getByName("client").compileClasspath)

	include("**/api/**")
	isFailOnError = false
}

val javadocJar by tasks.registering(Jar::class) {
	dependsOn(tasks.javadoc)
	from(tasks.javadoc.map { it.destinationDir })

	archiveClassifier.set("fatjavadoc")
}

tasks.build.configure { dependsOn(javadocJar) }

loom {
	runs.create("gametest") {
		inherit(runs["testmodServer"])
		name = "Game Test"

		vmArg("-Dfabric-api.gametest")
		vmArg("-Dfabric-api.gametest.report-file=${project.layout.buildDirectory.get()}/junit.xml")
		runDir = "build/gametest"
	}
}

tasks.test { dependsOn(":runGametest") }

subprojects {
	base.archivesName = project.name
	val mod = ModInfo()
	version = mod.version

	dependencies {
		"testmodImplementation"(sourceSets.main.map { it.output })
	}

	extensions.configure(PublishingExtension::class.java) {
		publications {
			create("mavenJava", MavenPublication::class.java) {
				artifact(tasks.remapJar) {
					builtBy(tasks.remapJar)
				}

				artifact(tasks.named("sourcesJar")) {
					builtBy(tasks.remapSourcesJar)
				}
			}
		}

		repositories {
			maven {
				name = "EchosMavenReleases"
				url = URI("https://maven.spiritstudios.dev/releases")
				credentials(PasswordCredentials::class)
			}
		}
	}

	tasks.javadoc.configure {
		isEnabled = false
	}
}

subprojects.forEach { tasks.remapJar.configure { dependsOn(":${it.name}:remapJar") } }

dependencies {
	afterEvaluate {
		subprojects.forEach {
			api(project(":${it.name}", "namedElements"))
			"clientImplementation"(project(":${it.name}").sourceSets["client"].output)
			include(project("${it.name}:"))


			compileOnly(rootProject.libs.tomlj)
			"testmodImplementation"(rootProject.libs.tomlj)
			"testmodImplementation"(project(":${it.name}").sourceSets["testmod"].output)
			"testmodClientImplementation"(project("${it.name}:").sourceSets["testmodClient"].output)
		}
	}
}

val remapMavenJar by tasks.registering(RemapJarTask::class) {
	inputFile.set(tasks.jar.flatMap { it.archiveFile })
	archiveFileName.set("${project.properties["archivesBaseName"]}-${project.version}-maven.jar")
	addNestedDependencies = false
	dependsOn(tasks.jar)
}

tasks.assemble.configure {
	dependsOn(remapMavenJar)
}

extensions.configure(PublishingExtension::class.java) {
	publications {
		val mavenJava by creating(MavenPublication::class) {
			artifact(remapMavenJar) {
				builtBy(remapMavenJar)
			}

			artifact(tasks.named("sourcesJar")) {
				builtBy(tasks.remapSourcesJar)
			}

			artifact(tasks.named("javadocJar"))

			pom.withXml {
				val depsNode = asNode().appendNode("dependencies")
				subprojects.forEach {
					if (it.name == "specter-debug") return@forEach

					val depNode = depsNode.appendNode("dependency")
					depNode.appendNode("groupId", it.group)
					depNode.appendNode("artifactId", it.name)
					depNode.appendNode("version", it.version)
					depNode.appendNode("scope", "compile")
				}
			}
		}

		@Suppress("UnstableApiUsage")
		loom.disableDeprecatedPomGeneration(mavenJava)
	}

	repositories {
		maven {
			name = "EchosMavenReleases"
			url = URI("https://maven.spiritstudios.dev/releases")
			credentials(PasswordCredentials::class)
		}
	}
}
