package at.rknoll.gradle.hardware

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator

class HardwareCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        println "-- Hardware Compile --"
		
		println project.hardwareSources
		
		CycleDetector<File, DefaultEdge> cycleDetector = new CycleDetector<File, DefaultEdge>(project.hardwareSources);
		if (cycleDetector.detectCycles()) {
			throw new RuntimeException("Detected cycles in source dependencies. Please resolve them.")
		}
		
		File file
		TopologicalOrderIterator<File, DefaultEdge> orderIterator
		orderIterator = new TopologicalOrderIterator<File, DefaultEdge>(project.hardwareSources)
		while (orderIterator.hasNext()) {
			file = orderIterator.next();
			println "$file"
			if (project.hardwareCompilers.find { it.compile(file) } == null) {
				println "could not compile $file.name"
			}
		}
    }

}
