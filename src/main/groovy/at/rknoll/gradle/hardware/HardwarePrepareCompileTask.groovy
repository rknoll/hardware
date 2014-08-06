package at.rknoll.gradle.hardware

import org.gradle.api.file.FileTree
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class HardwarePrepareCompileTask extends DefaultTask {

    @TaskAction
    def compile() {
		project.copy {
			from {
				project.configurations.compile.collect { project.zipTree(it).matching{exclude{it.path.contains('compile')}} }
			}
			into new File(project.projectDir, "libs/")
		}

		project.copy {
			from {
				project.configurations.compile.collect { project.zipTree(it).matching{include{it.path.contains('compile')}} }
			}
			into project.projectDir
		}

		/*FileTree sources = project.fileTree(dir: 'libs')

		sources.visit { file ->
			project.hardwareSources.addVertex(file.file)
		}*/

    }

}
