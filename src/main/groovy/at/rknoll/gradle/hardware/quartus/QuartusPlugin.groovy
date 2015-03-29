package at.rknoll.gradle.hardware.quartus

import at.rknoll.gradle.hardware.HardwareCompileTask
import at.rknoll.gradle.hardware.toplevel.TopLevelExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import at.rknoll.gradle.hardware.HardwareCompilerContainer
import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwarePlugin
import org.gradle.api.plugins.BasePlugin

class QuartusPlugin implements Plugin<Project> {
	public static final String SYNTHESIZE_TASK_NAME = "synthesize";

	def void apply(Project project) {
		project.getPlugins().apply(HardwarePlugin.class);

		project.extensions.quartus = new QuartusExtension()

		if (project.extensions.findByName("toplevel") == null) {
			project.extensions.toplevel = new TopLevelExtension()
		}

		def synDir = project.file("syn")
		if (!synDir.exists()) synDir.mkdir()
		if (synDir.isFile()) {
			throw new RuntimeException("Invalid synthesize directory '" + synDir.getAbsolutePath() + "'. If this is a File, please remove it.")
		}

		QuartusSynthesizeTask synthesize = project.getTasks().create(SYNTHESIZE_TASK_NAME, QuartusSynthesizeTask.class);
		synthesize.setDescription("Synthesize this project with Quartus.");
		synthesize.setGroup(BasePlugin.BUILD_GROUP);
		synthesize.dependsOn(HardwarePlugin.PREPARE_TASK_NAME);
		synthesize.outputs.dir synDir
		synthesize.outputs.upToDateWhen { false }

	}

}
