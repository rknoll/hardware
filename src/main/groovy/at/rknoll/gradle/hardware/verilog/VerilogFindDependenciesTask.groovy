package at.rknoll.gradle.hardware.verilog

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VerilogFindDependenciesTask extends SourceTask {

    @TaskAction
    def compile() {
        FileTree sources = getSource()

        sources.visit { file ->
			// find dependencies for this file
			/*project.sourceSets.main.getAllSource().each {
				if (!it.equals(file.file)) {
					project.hardwareSources.addEdge(it, file.file);
				}
			}*/
        }
    }

}
