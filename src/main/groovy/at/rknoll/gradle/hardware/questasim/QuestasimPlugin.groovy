package at.rknoll.gradle.hardware.questasim

import at.rknoll.gradle.hardware.HardwareCompilerContainer
import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwareUtils
import at.rknoll.gradle.hardware.toplevel.TopLevelExtension
import at.rknoll.gradle.hardware.vhdl.VhdlUtils
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
		HardwareCompilerContainer compilers = hardwareConvention.getHardwareCompilers()

		if (compilers.find { "questasim".equals(it.name) } == null) {
			compilers.create("questasim", {
				it.setDescription("compile with questasim")
				it.setHardwareCompilerImpl(new QuestasimCompilerImpl(project))
			});
		}

		project.tasks.sources.from(new File(project.file("compile"), HardwareUtils.getLibraryName(project.group, project.name))) {
			into 'compile/' + HardwareUtils.getLibraryName(project.group, project.name)
		}
	}

}
