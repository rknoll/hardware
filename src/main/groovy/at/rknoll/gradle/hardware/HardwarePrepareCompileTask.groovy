package at.rknoll.gradle.hardware

import org.gradle.api.file.FileTree
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class HardwarePrepareCompileTask extends DefaultTask {

    @TaskAction
    def compile() {
		project.copy {
			from {
				project.configurations.compile.collect { project.zipTree(it) }
			}
			into new File(project.projectDir, "libs/")
		}
		
		FileTree sources = project.fileTree(dir: 'libs')

		sources.visit { file ->
			project.hardwareSources.addVertex(file.file)
		}

    }

}
