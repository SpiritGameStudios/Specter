plugins {
	id("specter.module.conventions")
}

repositories {
	mavenCentral()
	maven("https://maven.nucleoid.xyz")
}

dependencies {
	implementation(project(":core", configuration = "namedElements"))

	specterImplementation(rootProject.libs.nightconfig.core)
	include(rootProject.libs.nightconfig.core)

	specterImplementation(rootProject.libs.nightconfig.toml)
	include(rootProject.libs.nightconfig.toml)
}
