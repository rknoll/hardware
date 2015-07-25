package at.rknoll.gradle.hardware.compiler.questasim

import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.toplevel.TopLevelExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class QuestasimPlugin implements Plugin<Project> {

    def void apply(Project project) {
        project.plugins.apply HardwarePlugin.class

        project.extensions.questasim = new QuestasimExtension()

        if (project.extensions.findByName('toplevel') == null) {
            project.extensions.toplevel = new TopLevelExtension()
        }

        if (project.hardwareCompilers.find { QuestasimCompilerImpl.NAME.equals(it.name) } == null) {
            project.hardwareCompilers.create(QuestasimCompilerImpl.NAME, {
                it.updateOrder project.hardwareCompilers
                it.setDescription "compile with questasim"
                it.setHardwareCompilerImpl new QuestasimCompilerImpl(project)
            })
        }
    }

}
