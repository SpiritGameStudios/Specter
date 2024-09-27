import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.SourceSetOutput
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

fun Project.moduleDependencies(project: Project, vararg modules: String) {
	val depList: MutableList<ProjectDependency> = mutableListOf()
	modules.forEach { depList.add(project.dependencies.project(":$it", "namedElements")) }

	val clientOutputs = mutableListOf<SourceSetOutput?>()
	modules.forEach {
		clientOutputs.add(findProject(":$it")?.extensions?.findByName("sourceSets")?.let {
			it as org.gradle.api.tasks.SourceSetContainer
		}?.getByName("client")?.output)
	}

	project.dependencies {
		depList.forEach {
			"implementation"(it)
			"testmodImplementation"(it)
		}
		clientOutputs.forEach {
			it?.let { output ->
				run {
					"clientImplementation"(output)
					"testmodClientImplementation"(output)
				}
			}
		}
	}
}





