moduleDependencies(project, "specter-core")

repositories {
	mavenCentral()
	maven("https://maven.nucleoid.xyz")
}

dependencies {
	implementation(rootProject.libs.nightconfig.core)
	include(rootProject.libs.nightconfig.core)

	implementation(rootProject.libs.nightconfig.toml)
	include(rootProject.libs.nightconfig.toml)

	modCompileOnly(rootProject.libs.stapi)
}
