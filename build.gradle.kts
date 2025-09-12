plugins {
	id("specter.common.conventions")
}

val main by sourceSets
val client by sourceSets
val testmod by sourceSets.creating
val testmodClient by sourceSets.creating

tasks.register<DefaultTask>("checkstyle") {
	dependsOn(tasks.checkstyleMain)
	dependsOn(tasks["checkstyleClient"])
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

			source(testmod)
		}

		create("testmodClient") {
			client()
			configName = "Testmod Client"
			source(testmodClient)
		}

		create("testmodServer") {
			server()
			name = "Testmod Server"
			source(testmod)
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

main.compileClasspath += mainClasspath
main.runtimeClasspath += mainClasspath

client.compileClasspath += clientClasspath
client.runtimeClasspath += clientClasspath

testmod.compileClasspath += main.compileClasspath
testmod.runtimeClasspath += main.runtimeClasspath

testmodClient.compileClasspath += client.compileClasspath
testmodClient.runtimeClasspath += client.runtimeClasspath

testmod.compileClasspath += testmodClasspath
testmod.runtimeClasspath += testmodClasspath

testmodClient.compileClasspath += testmod.compileClasspath
testmodClient.runtimeClasspath += testmod.runtimeClasspath

val fatJavadoc by tasks.registering(Javadoc::class) {
	group = "documentation"

	with(options as StandardJavadocDocletOptions) {
		source = "21"
		encoding = "UTF-8"
		charset("UTF-8")
		memberLevel = JavadocMemberLevel.PACKAGE
		addStringOption("Xdoclint:none", "-quiet")
		links(
			"https://maven.fabricmc.net/docs/yarn-${libs.versions.yarn.get()}/",
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

	classpath = files(main.compileClasspath, client.compileClasspath)
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

