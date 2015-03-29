package at.rknoll.gradle.hardware

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import org.jgrapht.ext.DOTExporter
import org.jgrapht.ext.VertexNameProvider

class HardwareCompileTask extends SourceTask {

    @TaskAction
    def compile() {
		DOTExporter exporter = new DOTExporter(new VertexNameProvider<File>() {
			String getVertexName(File file) {
				String filename = file.name
				return filename.replace('-', '_').replace('.', '_')
			}
		}, null, null, null, null);
		File graphDir = new File(project.projectDir, "graph")
		graphDir.mkdirs();
		exporter.export(new FileWriter(new File(graphDir, "dependencies.dot")), project.hardwareSources);

		CycleDetector<File, DefaultEdge> cycleDetector = new CycleDetector<File, DefaultEdge>(project.hardwareSources);
		if (cycleDetector.detectCycles()) {
			throw new RuntimeException("Detected cycles in source dependencies. Please resolve them.")
		}

		File file
		TopologicalOrderIterator<File, DefaultEdge> orderIterator
		orderIterator = new TopologicalOrderIterator<File, DefaultEdge>(project.hardwareSources)
		while (orderIterator.hasNext()) {
			file = orderIterator.next();
			println "compiling $file.name"
			def compatibleCompilers = project.hardwareCompilers.findAll { it.compile(file) }
			if (compatibleCompilers.size() == 0) {
				throw new RuntimeException("could not find a compiler for $file.name")
			} else if (compatibleCompilers.size() != 1) {
				throw new RuntimeException("multiple compilers found for $file.name [" + compatibleCompilers.name.join(', ') + "]")
			} else {
				println "compiled using " + compatibleCompilers[0].name
			}
		}
    }

}
