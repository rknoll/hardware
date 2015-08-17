package at.rknoll.gradle.hardware

import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.jgrapht.alg.CycleDetector
import org.jgrapht.ext.DOTExporter
import org.jgrapht.ext.VertexNameProvider
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator

class HardwareCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        def convention = project.convention.getPlugin HardwarePluginConvention

        def exporter = new DOTExporter(new VertexNameProvider<File>() {
            String getVertexName(File file) {
                String filename = file.name
                return filename.replace('-', '_').replace('.', '_')
            }
        }, null, null, null, null)
        File graphDir = new File(project.projectDir, "graph")
        graphDir.mkdirs()
        exporter.export(new FileWriter(new File(graphDir, "dependencies.dot")), convention.hardwareSources)

        def cycleDetector = new CycleDetector<File, DefaultEdge>(convention.hardwareSources)
        if (cycleDetector.detectCycles()) {
            throw new RuntimeException("Detected cycles in source dependencies. Please resolve them.")
        }

        File file
        def orderIterator = new TopologicalOrderIterator<File, DefaultEdge>(convention.hardwareSources)
        while (orderIterator.hasNext()) {
            file = orderIterator.next();
            println "compiling $file.name"

            def allCompilers = new ArrayList<HardwareCompiler>(convention.hardwareCompilers);
            allCompilers = allCompilers.sort()

            def usedCompiler = allCompilers.find { it.compile(file) }

            if (usedCompiler == null) {
                throw new RuntimeException("could not find a compiler for $file.name")
            } else {
                println "compiled using " + usedCompiler.name
            }
        }
    }

}
