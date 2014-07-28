package at.rknoll.gradle.hardware.vhdl

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VhdlCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        println "-- Vhdl Compile --"

        FileTree sources = getSource()

        sources.visit { file ->
            println "$file.relativePath => $file.file"
        }
    }

}
