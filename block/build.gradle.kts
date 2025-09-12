plugins {
	id("specter.module.conventions")
}

dependencies {
	implementation(project(":core", configuration = "namedElements"))
	implementation(project(":item", configuration = "namedElements"))
	implementation(project(":registry", configuration = "namedElements"))
}
