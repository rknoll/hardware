package at.rknoll.gradle.hardware.compiler.modelsimaltera

import at.rknoll.gradle.hardware.HardwareCompiler
import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.HardwarePluginConvention
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project

class ModelsimAlteraPlugin implements Plugin<Project> {

    def void apply(Project project) {
        project.getPlugins().apply(HardwarePlugin.class);

        project.extensions.modelsimaltera = new ModelsimAlteraExtension()

        HardwarePluginConvention hardwareConvention = project.getConvention().getPlugin(HardwarePluginConvention.class)
        NamedDomainObjectContainer<HardwareCompiler> compilers = hardwareConvention.getHardwareCompilers()

        if (compilers.find { ModelsimAlteraCompilerImpl.NAME.equals(it.name) } == null) {
            compilers.create(ModelsimAlteraCompilerImpl.NAME, {
                it.updateOrder(compilers)
                it.setDescription("compile with modelsimaltera")
                it.setHardwareCompilerImpl(new ModelsimAlteraCompilerImpl(project))
            });
        }
    }

}
