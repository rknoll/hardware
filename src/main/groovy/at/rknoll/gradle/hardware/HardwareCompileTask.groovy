package at.rknoll.gradle.hardware

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.jgrapht.alg.CycleDetector
import org.jgrapht.ext.DOTExporter
import org.jgrapht.ext.VertexNameProvider
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator

class HardwareCompileTask extends DefaultTask {

    @TaskAction
    def compile() {
        def convention = project.convention.getPlugin HardwarePluginConvention
        def compiled = new HashSet<File>()

        project.sourceSets.all([execute: { SourceSet sourceSet ->
            def exporter = new DOTExporter(new VertexNameProvider<File>() {
                String getVertexName(File file) {
                    String filename = file.name
                    return filename.replace('-', '_').replace('.', '_')
                }
            }, null, null, null, null)
            File graphDir = new File(project.projectDir, "graph-" + sourceSet.name)
            graphDir.mkdirs()
            exporter.export(new FileWriter(new File(graphDir, "dependencies.dot")),
                    convention.hardwareSources[sourceSet.name])

            def cycleDetector = new CycleDetector<File, DefaultEdge>(convention.hardwareSources[sourceSet.name])
            if (cycleDetector.detectCycles()) {
                throw new RuntimeException("Detected cycles in source dependencies. Please resolve them.")
            }

            File file
            def orderIterator = new TopologicalOrderIterator<File, DefaultEdge>(convention.hardwareSources[sourceSet.name])
            while (orderIterator.hasNext()) {
                file = orderIterator.next();
                if (compiled.contains(file)) continue

                println "compiling $file.name"

                compiled.add(file)

                def allCompilers = new ArrayList<HardwareCompiler>(convention.hardwareCompilers);
                allCompilers = allCompilers.sort()

                def usedCompiler = allCompilers.find { it.compile(file) }

                if (usedCompiler == null) {
                    throw new RuntimeException("could not find a compiler for $file.name")
                } else {
                    println "compiled using " + usedCompiler.name
                }
            }
        }] as Action<SourceSet>)
    }

}
