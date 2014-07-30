package at.rknoll.gradle.hardware.verilog

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VerilogPrepareCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        println "-- Verilog Prepare Compile --"

        FileTree sources = getSource()

        sources.visit { file ->
			println "found Verilog source: $file.name"
			project.hardwareSources.addVertex(file.file)
        }
    }

}
