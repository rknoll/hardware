package at.rknoll.gradle.hardware.vhdl

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VhdlPrepareCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        println "-- Vhdl Prepare Compile --"
		
        FileTree sources = getSource()

        sources.visit { file ->
			println "found Vhdl source: $file.name"
			project.hardwareSources.addVertex(file.file)
        }
    }

}
