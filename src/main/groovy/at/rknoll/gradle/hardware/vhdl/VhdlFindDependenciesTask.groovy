package at.rknoll.gradle.hardware.vhdl

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator

class VhdlFindDependenciesTask extends SourceTask {

    @TaskAction
    def compile() {
        FileTree sources = getSource()

		// depend on all external dependencies
		for (File file : project.hardwareSources.vertexSet()) {
			if (!sources.contains(file)) {
				sources.visit { src ->
					project.hardwareSources.addEdge(file, src.file);
				}
			}
		}

		// TODO: find internal dependencies for this file
        /*sources.visit { file ->

        }*/
    }

}
