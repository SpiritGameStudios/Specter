plugins {
	id("specter.module.conventions")
}

repositories {
	maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
	implementation(project(":core", configuration = "namedElements"))
	implementation(project(":gui", configuration = "namedElements"))
	implementation(project(":serialization", configuration = "namedElements"))

	modCompileOnly(rootProject.libs.modmenu)
}
