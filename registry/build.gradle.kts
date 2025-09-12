plugins {
	id("specter.module.conventions")
}

dependencies {
	implementation(project(":core", configuration = "namedElements"))
	implementation(project(":serialization", configuration = "namedElements"))
}
