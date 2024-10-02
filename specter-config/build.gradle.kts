moduleDependencies(project, "specter-core", "specter-gui", "specter-serialization")

repositories {
	maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
	modCompileOnly("com.terraformersmc:modmenu:${property("deps.modmenu")}")
}
