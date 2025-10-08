plugins {
	id("specter.module.conventions")
}

dependencies {
	implementation(project(":core", configuration = "namedElements"))
}
