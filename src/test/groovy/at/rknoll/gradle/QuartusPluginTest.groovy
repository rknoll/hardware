package at.rknoll.gradle

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class QuartusPluginTest {

	@Test
	public void quartusPluginAddsSynthesizeTaskToProject() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'quartus'
		println 'quartusPluginAddsSynthesizeTaskToProject'
		assertTrue(project.tasks.synthesize instanceof QuartusSynthesizeTask)
	}

}
