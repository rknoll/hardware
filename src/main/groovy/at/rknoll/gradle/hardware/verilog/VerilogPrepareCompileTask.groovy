package at.rknoll.gradle.hardware.verilog

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VerilogPrepareCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        FileTree sources = getSource()

        sources.visit { file ->
			project.hardwareSources.addVertex(file.file)
        }
    }

}
