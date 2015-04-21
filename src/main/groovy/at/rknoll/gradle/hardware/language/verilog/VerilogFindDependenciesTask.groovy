package at.rknoll.gradle.hardware.language.verilog

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VerilogFindDependenciesTask extends DefaultTask {

    @TaskAction
    def compile() {
        //FileTree sources = getSource()

		// depend on all external dependencies
		/*for (File file : project.hardwareSources.vertexSet()) {
			if (!sources.contains(file)) {
				sources.visit { src ->
					project.hardwareSources.addEdge(file, src.file);
				}
			}
		}*/

		// TODO: find internal dependencies for this file
        /*sources.visit { file ->

        }*/
    }

}
