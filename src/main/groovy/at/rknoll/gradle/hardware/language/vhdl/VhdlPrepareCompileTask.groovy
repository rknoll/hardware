package at.rknoll.gradle.hardware.language.vhdl

import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwareSourceInformation
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails
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

        def convention = project.convention.getPlugin HardwarePluginConvention

        sources.visit { FileVisitDetails details ->
            if (!details.isDirectory()) {
                convention.hardwareSourceInformation[details.file] = source
                convention.hardwareSources.addVertex(details.file)
            }
        }
    }

}
