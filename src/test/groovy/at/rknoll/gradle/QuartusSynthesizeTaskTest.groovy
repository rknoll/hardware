package at.rknoll.gradle

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class QuartusSynthesizeTaskTest {

	@Test
	public void canAddTaskToProject() {
		Project project = ProjectBuilder.builder().build()
		def task = project.task('synthesizeTest', type: QuartusSynthesizeTask)
		assertTrue(task instanceof QuartusSynthesizeTask)
	}

}
