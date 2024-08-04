moduleDependencies(project, "specter-base")

repositories {
	maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
	modCompileOnly("com.terraformersmc:modmenu:${property("deps.modmenu")}")
}
