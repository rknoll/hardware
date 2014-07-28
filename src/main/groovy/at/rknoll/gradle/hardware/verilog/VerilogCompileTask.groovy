package at.rknoll.gradle.hardware.verilog

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VerilogCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        println "-- Verilog Compile --"

        FileTree sources = getSource()

        sources.visit { file ->
            println "$file.relativePath => $file.file"
        }
    }

}
