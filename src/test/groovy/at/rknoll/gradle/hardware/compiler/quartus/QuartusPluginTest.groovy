package at.rknoll.gradle.hardware.compiler.quartus

import at.rknoll.gradle.hardware.compiler.quartus.tasks.QuartusSynthesizeTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class QuartusPluginTest {

    @Test
    public void quartusPluginAddsSynthesizeTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'quartus'
        assertTrue project.tasks.synthesize instanceof QuartusSynthesizeTask
    }

}
