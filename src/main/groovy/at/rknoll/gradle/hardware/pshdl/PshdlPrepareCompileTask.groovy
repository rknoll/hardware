package at.rknoll.gradle.hardware.pshdl

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class PshdlPrepareCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        println "-- Pshdl Prepare Compile --"
		
		File destDir = project.file("generated/pshdl")
		if (destDir.isDirectory()) destDir.deleteDir()
		destDir.mkdirs()

        FileTree sources = getSource()

        sources.visit { file ->
			println "found Pshdl source: $file.name"
			File destFile = new File(destDir, file.name + ".vhdl")
			destFile.bytes = file.file.bytes
			project.hardwareSources.addVertex(destFile)

			// so that other sources can reference the pshdl source as dependency..
			project.hardwareSources.addVertex(file.file)
			project.hardwareSources.addEdge(destFile, file.file);
        }
    }

}
