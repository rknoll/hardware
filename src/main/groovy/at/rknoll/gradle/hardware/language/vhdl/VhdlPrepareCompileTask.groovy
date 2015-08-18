package at.rknoll.gradle.hardware.language.vhdl

import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwareSourceInformation
import org.gradle.api.Action
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VhdlPrepareCompileTask extends SourceTask {
    public String sourceSet

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
                convention.hardwareSourceInformation[sourceSet][details.file] = source
                convention.hardwareSources[sourceSet].addVertex(details.file)
                if (SourceSet.MAIN_SOURCE_SET_NAME.equals(sourceSet)) {
                    project.sourceSets.all([execute: { SourceSet sourceSet ->
                        if (!convention.hardwareSources[sourceSet.name].containsVertex(details.file)) {
                            convention.hardwareSourceInformation[sourceSet.name][details.file] = source
                            convention.hardwareSources[sourceSet.name].addVertex(details.file)
                        }
                    }] as Action<SourceSet>)
                }
            }
        }
    }

}
