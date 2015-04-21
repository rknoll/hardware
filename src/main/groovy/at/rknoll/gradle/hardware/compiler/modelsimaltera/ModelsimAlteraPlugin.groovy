package at.rknoll.gradle.hardware.compiler.modelsimaltera

import org.gradle.api.Plugin
import org.gradle.api.Project
import at.rknoll.gradle.hardware.HardwareCompilerContainer
import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwarePlugin

class ModelsimAlteraPlugin implements Plugin<Project> {

	def void apply(Project project) {
		project.getPlugins().apply(HardwarePlugin.class);

        project.extensions.modelsimaltera = new ModelsimAlteraExtension()

        HardwarePluginConvention hardwareConvention = project.getConvention().getPlugin(HardwarePluginConvention.class)
		HardwareCompilerContainer compilers = hardwareConvention.getHardwareCompilers()

		if (compilers.find { ModelsimAlteraCompilerImpl.NAME.equals(it.name) } == null) {
			compilers.create(ModelsimAlteraCompilerImpl.NAME, {
				it.setDescription("compile with modelsimaltera")
				it.setHardwareCompilerImpl(new ModelsimAlteraCompilerImpl(project))
			});
		}
	}

}
