package at.rknoll.gradle

import org.gradle.api.*

class QuartusPlugin implements Plugin<Project> {

	def void apply(Project project) {
        project.extensions.quartus = new QuartusExtension()
		project.task('synthesize', type: QuartusSynthesizeTask)
	}
	
}
