package at.rknoll.gradle

import org.gradle.api.*

class QuartusPlugin implements Plugin<Project> {
	
	def void apply(Project project) {
		project.task('synthesize', type: QuartusSynthesizeTask)
	}
	
}
