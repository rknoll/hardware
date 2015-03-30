package at.rknoll.gradle.hardware.vhdl

import at.rknoll.gradle.hardware.HardwareSourceInformation
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VhdlPrepareCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        FileTree sources = getSource()

        def source = new HardwareSourceInformation()
        source.group = project.group
        source.name = project.name
        source.version = project.version

        sources.visit { file ->
            project.hardwareSourceInformation[file.file] = source
			project.hardwareSources.addVertex(file.file)
        }
    }

}
