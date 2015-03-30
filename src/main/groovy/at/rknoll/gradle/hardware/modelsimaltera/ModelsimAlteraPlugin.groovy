package at.rknoll.gradle.hardware.modelsimaltera

import at.rknoll.gradle.hardware.HardwareUtils
import at.rknoll.gradle.hardware.vhdl.VhdlUtils
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

		if (compilers.find { "modelsimaltera".equals(it.name) } == null) {
			compilers.create("modelsimaltera", {
				it.setDescription("compile with modelsimaltera")
				it.setHardwareCompilerImpl(new ModelsimAlteraCompilerImpl(project))
			});
		}

		project.tasks.sources.from(new File(project.file("compile"), HardwareUtils.getLibraryName(project.group, project.name))) {
			into 'compile/' + HardwareUtils.getLibraryName(project.group, project.name)
		}
	}

}
