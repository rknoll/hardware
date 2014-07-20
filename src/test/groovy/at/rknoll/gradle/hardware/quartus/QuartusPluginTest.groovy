package at.rknoll.gradle.hardware.quartus

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class QuartusPluginTest {

	@Test
	public void quartusPluginAddsSynthesizeTaskToProject() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'quartus'
		println 'quartusPluginAddsSynthesizeTaskToProject'
		assertTrue(project.tasks.synthesize instanceof QuartusSynthesizeTask)
	}

}
