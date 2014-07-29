package at.rknoll.gradle.hardware.quartus

import org.gradle.api.Plugin
import org.gradle.api.Project
import at.rknoll.gradle.hardware.HardwareCompilerContainer
import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwarePlugin

class QuartusPlugin implements Plugin<Project> {

	def void apply(Project project) {
		project.getPlugins().apply(HardwarePlugin.class);

        project.extensions.quartus = new QuartusExtension()
		project.task('synthesize', type: QuartusSynthesizeTask)

        HardwarePluginConvention hardwareConvention = project.getConvention().getPlugin(HardwarePluginConvention.class)
		HardwareCompilerContainer compilers = hardwareConvention.getHardwareCompilers()

		if (compilers.find { "quartus".equals(it.name) } == null) {
			compilers.create("quartus", {
				it.setDescription("compile with quartus")
			});
		}

	}

}
