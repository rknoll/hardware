package at.rknoll.gradle.hardware.compiler.quartus

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class QuartusSynthesizeTaskTest {

    @Test
    public void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task 'synthesizeTest', type: QuartusSynthesizeTask
        assertTrue task instanceof QuartusSynthesizeTask
    }

}
