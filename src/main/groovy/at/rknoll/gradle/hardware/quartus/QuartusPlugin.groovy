package at.rknoll.gradle.hardware.quartus

import org.gradle.api.Plugin
import org.gradle.api.Project

class QuartusPlugin implements Plugin<Project> {

	def void apply(Project project) {
        project.extensions.quartus = new QuartusExtension()
		project.task('synthesize', type: QuartusSynthesizeTask)
	}
	
}
