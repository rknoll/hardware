package at.rknoll.gradle.hardware.compiler.modelsimaltera

import at.rknoll.gradle.hardware.HardwarePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ModelsimAlteraPlugin implements Plugin<Project> {

    def void apply(Project project) {
        project.plugins.apply HardwarePlugin.class

        project.extensions.modelsimaltera = new ModelsimAlteraExtension()

        if (project.hardwareCompilers.find { ModelsimAlteraCompilerImpl.NAME.equals(it.name) } == null) {
            project.hardwareCompilers.create(ModelsimAlteraCompilerImpl.NAME, {
                it.updateOrder project.hardwareCompilers
                it.setDescription "compile with modelsimaltera"
                it.setHardwareCompilerImpl new ModelsimAlteraCompilerImpl(project)
            })
        }
    }

}
