import net.fabricmc.loom.task.RemapJarTask
import java.net.URI

plugins {
	`java-library`
	`maven-publish`
	id("fabric-loom") version "1.7-SNAPSHOT"
}

class ModInfo {
	val id = property("mod.id").toString()
	val group = property("mod.group").toString()
	val version = property("mod.version").toString()
}

class Dependencies {
	val minecraft = property("deps.minecraft").toString()
	val loader = property("deps.loader").toString()
	val yarn = property("deps.yarn").toString()

	val fabricApi = property("deps.fabricapi").toString()
}

allprojects {
	apply(plugin = "java-library")
	apply(plugin = "maven-publish")
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

	val mod = ModInfo()
	val deps = Dependencies()

	group = mod.group
	version = mod.version

	repositories {
		maven("https://maven.terraformersmc.com/releases/")
	}

	dependencies {
		minecraft("com.mojang:minecraft:${deps.minecraft}")
		mappings("net.fabricmc:yarn:${deps.yarn}:v2")
		modImplementation("net.fabricmc:fabric-loader:${deps.loader}")

		modImplementation("net.fabricmc.fabric-api:fabric-api:${deps.fabricApi}")
	}

	tasks.withType<ProcessResources> {
		inputs.property("id", mod.id)
		inputs.property("version", mod.version)
		inputs.property("loader_version", deps.loader)
		inputs.property("minecraft_version", deps.minecraft)

		val map = mapOf(
			"id" to mod.id,
			"version" to mod.version,
			"loader_version" to deps.loader,
			"minecraft_version" to deps.minecraft
		)

		filesMatching("fabric.mod.json") { expand(map) }
	}

	loom {
		splitEnvironmentSourceSets()
		if (file("src/main/resources/${project.name}.accesswidener").exists()) accessWidenerPath =
			file("src/main/resources/${project.name}.accesswidener")
	}

	for (modProject in allprojects) {
		loom.mods.register(modProject.name) {
			sourceSet(modProject.sourceSets.getByName("main"))
			sourceSet(modProject.sourceSets.getByName("client"))
		}

		loom.mods.register(modProject.name + "-testmod") {
			sourceSet(modProject.sourceSets.getByName("testmod"))
			sourceSet(modProject.sourceSets.getByName("testmodClient"))
		}
	}

	tasks.withType<GenerateModuleMetadata>().configureEach {
		enabled = false
	}
}

tasks.javadoc {
	with(options as StandardJavadocDocletOptions) {
		source = "21"
		encoding = "UTF-8"
		charset("UTF-8")
		memberLevel = JavadocMemberLevel.PACKAGE
		addStringOption("Xdoclint:none", "-quiet")
	}

	allprojects.forEach { proj -> source(proj.sourceSets.main.map { it.allJava.srcDirs }) }

	classpath = project.files(sourceSets.main.map { it.compileClasspath })
	include("**/api/**")
	isFailOnError = false
}

val javadocJar by tasks.registering(Jar::class) {
	dependsOn(tasks.javadoc)
	from(tasks.javadoc.map { it.destinationDir!! })

	archiveClassifier.set("fatjavadoc")
}

tasks.assemble.configure {
	dependsOn(javadocJar)
}

tasks.test.configure {
	dependsOn(tasks.named("runGametest"))
}

subprojects {
	version = rootProject.version

	sourceSets.create("testmod") {
		compileClasspath += sourceSets.main.get().compileClasspath
		runtimeClasspath += sourceSets.main.get().runtimeClasspath
	}

	sourceSets.create("testmodClient") {
		compileClasspath += sourceSets.main.get().compileClasspath
		runtimeClasspath += sourceSets.main.get().runtimeClasspath

		compileClasspath += sourceSets.getByName("client").compileClasspath
		runtimeClasspath += sourceSets.getByName("client").runtimeClasspath

		compileClasspath += sourceSets.getByName("testmod").compileClasspath
		runtimeClasspath += sourceSets.getByName("testmod").runtimeClasspath
	}

	dependencies {
		"testmodImplementation"(sourceSets.main.map { it.output })

		"testmodClientImplementation"(sourceSets.getByName("client").output)
		"testmodClientImplementation"(sourceSets.getByName("testmod").output)
	}

	extensions.configure(PublishingExtension::class.java) {
		publications {
			create("mavenJava", MavenPublication::class.java) {
				from(components.getByName("java"))
			}
		}

		repositories {
			maven {
				name = "EchosMavenReleases"
				url = URI("https://maven.callmeecho.dev/releases")
				credentials(PasswordCredentials::class)
			}
		}
	}

	tasks.javadoc.configure {
		isEnabled = false
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
			url = URI("https://maven.callmeecho.dev/releases")
			credentials(PasswordCredentials::class)
		}
	}
}

sourceSets.create("testmod") {
	compileClasspath += sourceSets.main.get().compileClasspath
	runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

sourceSets.create("testmodClient") {
	compileClasspath += sourceSets.main.get().compileClasspath
	runtimeClasspath += sourceSets.main.get().runtimeClasspath

	compileClasspath += sourceSets.getByName("client").compileClasspath
	runtimeClasspath += sourceSets.getByName("client").runtimeClasspath

	compileClasspath += sourceSets.getByName("testmod").compileClasspath
	runtimeClasspath += sourceSets.getByName("testmod").runtimeClasspath
}

loom {
	runs.create("testmodClient") {
		client()
		ideConfigGenerated(project.rootProject == project)
		name = "Testmod Client"
		source(sourceSets.getByName("testmodClient"))
	}

	runs.create("testmodServer") {
		server()
		ideConfigGenerated(project.rootProject == project)
		name = "Testmod Server"
		source(sourceSets.getByName("testmod"))
	}

	runs.create("gametest") {
		server()
		ideConfigGenerated(project.rootProject == project)
		name = "Game Test"
		source(sourceSets.getByName("testmod"))

		vmArg("-Dfabric-api.gametest")
		vmArg("-Dfabric-api.gametest.report-file=${project.layout.buildDirectory.get()}/junit.xml")
		runDir = "build/gametest"
	}
}

dependencies {
	afterEvaluate {
		subprojects.forEach {
			api(project(":${it.name}", "namedElements"))
			"clientImplementation"(project(":${it.name}").sourceSets.getByName("client").output)
			include(project(":${it.name}"))

			"testmodImplementation"(project(":${it.name}").sourceSets["testmod"].output)
			"testmodClientImplementation"(project(":${it.name}").sourceSets["testmodClient"].output)
		}
	}
}

for (subproject in subprojects) tasks.remapJar.configure { dependsOn(":${subproject.name}:remapJar") }
