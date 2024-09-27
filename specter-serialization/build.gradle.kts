moduleDependencies(project, "specter-core")

dependencies {
	implementation("org.tomlj:tomlj:${property("deps.tomlj")}")
	include("org.tomlj:tomlj:${property("deps.tomlj")}");
}
