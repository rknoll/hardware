package at.rknoll.gradle.hardware.compiler.questasim

import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.HardwarePluginConvention
import org.gradle.api.Plugin
import org.gradle.api.Project

class QuestasimPlugin implements Plugin<Project> {
    public static final String NAME = "questasim"

    def void apply(Project project) {
        project.plugins.apply HardwarePlugin

        project.extensions.create NAME, QuestasimExtension

        def convention = project.convention.getPlugin HardwarePluginConvention
        def compilers = convention.hardwareCompilers

        if (compilers.findByName(NAME) == null) {
            compilers.create(NAME, {
                it.updateOrder compilers
                it.setDescription "compile with " + NAME
                it.setHardwareCompilerImpl new QuestasimCompilerImpl(project)
            })
        }
    }

}
