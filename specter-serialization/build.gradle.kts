moduleDependencies(project, "specter-core")

repositories {
	mavenCentral()
	maven("https://maven.nucleoid.xyz")
}

dependencies {
	implementation(rootProject.libs.tomlj)
	include(rootProject.libs.tomlj)

	modCompileOnly(rootProject.libs.stapi)
}
