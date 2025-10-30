plugins {
	id("specter.common.conventions")
}

sourceSets {
	create("testmod")
	create("testmodClient")
}

tasks.register<DefaultTask>("checkstyle") {
	dependsOn(tasks.checkstyleMain)
	dependsOn(tasks.checkstyleClient)
	dependsOn(tasks["checkstyleTestmod"])
}

tasks.test { dependsOn(":runGametest") }

loom {
	runs {
		create("gametest") {
			server()
			name = "Game Test"

			vmArg("-Dfabric-api.gametest")
			vmArg("-Dfabric-api.gametest.report-file=${project.layout.buildDirectory.get()}/junit.xml")
			runDir = "build/gametest"

			source(sourceSets["testmod"])
		}

		create("testmodClient") {
			client()
			configName = "Testmod Client"
			source(sourceSets["testmodClient"])
		}

		create("testmodServer") {
			server()
			name = "Testmod Server"
			source(sourceSets["testmod"])
		}

		configureEach {
			vmArgs("-Dmixin.debug.verify=true")
		}
	}
}

tasks.remapJar {
	enabled = false
}

tasks.jar {
	enabled = false
}

val testmodClasspath by configurations.creating
val mainClasspath by configurations.creating
val clientClasspath by configurations.creating

dependencies {
	for (subproject in subprojects) {
		subproject.afterEvaluate {
			mainClasspath(project(subproject.path, "namedElements"))
			mainClasspath(project(subproject.path, "specterImplementation"))
			clientClasspath(subproject.sourceSets["client"].output)

			testmodClasspath(subproject.sourceSets["testmod"].output)
		}
	}
}

sourceSets {
	main {
		compileClasspath += mainClasspath
		runtimeClasspath += mainClasspath
	}

	client {
		compileClasspath += clientClasspath
		runtimeClasspath += clientClasspath
	}

	val testmod = getByName("testmod") {
		compileClasspath += main.get().compileClasspath
		runtimeClasspath += main.get().runtimeClasspath

		compileClasspath += testmodClasspath
		runtimeClasspath += testmodClasspath
	}

	getByName("testmodClient") {
		compileClasspath += client.get().compileClasspath
		runtimeClasspath += client.get().runtimeClasspath

		compileClasspath += testmod.compileClasspath
		runtimeClasspath += testmod.runtimeClasspath
	}
}

val fatJavadoc by tasks.registering(Javadoc::class) {
	group = "documentation"

	with(options as StandardJavadocDocletOptions) {
		source = "21"
		encoding = "UTF-8"
		charset("UTF-8")
		memberLevel = JavadocMemberLevel.PACKAGE
		addStringOption("Xdoclint:none", "-quiet")
		links(
			"https://guava.dev/releases/33.3.1-jre/api/docs/",
			"https://asm.ow2.io/javadoc/",
			"https://docs.oracle.com/en/java/javase/21/docs/api/",
		)

		include("**/api/**")

		isFailOnError = false
	}

	subprojects.forEach { proj ->
		source(proj.sourceSets.main.get().allJava.srcDirs)
		source(proj.sourceSets["client"].allJava.srcDirs)
	}

	classpath = files(sourceSets.main.get().compileClasspath, sourceSets["client"].compileClasspath)
}

val fatJavadocJar by tasks.registering(Jar::class) {
	group = "build"
	archiveClassifier.set("fatjavadoc")

	dependsOn(fatJavadoc)
	from(fatJavadoc.get().destinationDir)

}

publishing {
	publications {
		val mavenJava by creating(MavenPublication::class) {
			artifact(fatJavadocJar)

			pom.withXml {
				val dependenciesNode = asNode().appendNode("dependencies")
				subprojects.forEach {
					if (it.name == "specter-debug") return@forEach

					val dependencyNode = dependenciesNode.appendNode("dependency")
					dependencyNode.appendNode("groupId", it.group)
					dependencyNode.appendNode("artifactId", it.name)
					dependencyNode.appendNode("version", it.version)
					dependencyNode.appendNode("scope", "compile")
				}
			}
		}

		@Suppress("UnstableApiUsage")
		loom.disableDeprecatedPomGeneration(mavenJava)
	}
}

