package at.rknoll.gradle.hardware.compiler.questasim

import at.rknoll.gradle.hardware.HardwareCompiler
import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.toplevel.TopLevelExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project

class QuestasimPlugin implements Plugin<Project> {

    def void apply(Project project) {
        project.getPlugins().apply(HardwarePlugin.class);

        project.extensions.questasim = new QuestasimExtension()
        if (project.extensions.findByName('toplevel') == null) {
            project.extensions.toplevel = new TopLevelExtension()
        }

        HardwarePluginConvention hardwareConvention = project.getConvention().getPlugin(HardwarePluginConvention.class)
        NamedDomainObjectContainer<HardwareCompiler> compilers = hardwareConvention.getHardwareCompilers()

        if (compilers.find { QuestasimCompilerImpl.NAME.equals(it.name) } == null) {
            compilers.create(QuestasimCompilerImpl.NAME, {
                it.updateOrder(compilers)
                it.setDescription("compile with questasim")
                it.setHardwareCompilerImpl(new QuestasimCompilerImpl(project))
            });
        }
    }

}
