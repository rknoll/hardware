package at.rknoll.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class QuartusSynthesizeTask extends DefaultTask {

	@TaskAction
	def synthesize() {
		println "-- Quartus Synthesize --"
		println "Quartus Path: " + QuartusUtils.findQuartusExecutable("C:\\")
	}

}
