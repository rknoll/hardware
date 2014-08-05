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
				int pos = filename.lastIndexOf('.');
				filename = pos > 0 ? filename.substring(0, pos) : filename;
				return filename.replace('-', '_')
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
			if (project.hardwareCompilers.find { it.compile(file) } == null) {
				println "could not compile $file.name"
			}
		}
    }

}
