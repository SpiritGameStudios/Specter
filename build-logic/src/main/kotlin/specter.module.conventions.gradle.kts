import org.gradle.accessors.dm.LibrariesForLibs

plugins {
	id("specter.common.conventions")
}

base.archivesName = project.name

val main by sourceSets
val client by sourceSets

val specterImplementation by configurations.creating { extendsFrom(configurations.implementation.get()) }

main.compileClasspath += specterImplementation
main.runtimeClasspath += specterImplementation

val testmod by sourceSets.creating {
	compileClasspath += main.compileClasspath
	runtimeClasspath += main.runtimeClasspath

	compileClasspath += client.compileClasspath
	runtimeClasspath += client.runtimeClasspath
}

loom {
	mods {
		register(name) {
			sourceSet(main)
			sourceSet(client)
		}

		register("${name}-testmod") {
			sourceSet(testmod)
		}
	}
}

dependencies {
	"testmodImplementation"(main.output)
	"testmodImplementation"(client.output)
}

for (sourceSet in arrayOf(main, client)) {
	val task = tasks.register<GeneratePackageInfosTask>(sourceSet.getTaskName("generate", "PackageInfos")) {
		group = "codegen"

		root = file("src/${sourceSet.name}/java")
		output = file("src/generated/${sourceSet.name}")
	}

	sourceSet.java.srcDir(task)

	val cleanTask = tasks.register<Delete>(sourceSet.getTaskName("clean", "PackageInfos")) {
		group = "codegen"
		delete(file("src/generated/${sourceSet.name}"))
	}

	tasks.clean.configure { dependsOn(cleanTask) }
}

java {
	withJavadocJar()
}

val libs = the<LibrariesForLibs>()

tasks.javadoc {
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

	dependsOn(tasks.withType<GeneratePackageInfosTask>())
}

publishing {
	publications {
		val mavenJava by creating(MavenPublication::class) {
			artifact(tasks.remapJar) { builtBy(tasks.remapJar) }
			artifact(tasks.remapSourcesJar) { builtBy(tasks.remapSourcesJar) }
		}
	}
}


