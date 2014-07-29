package at.rknoll.gradle.hardware

import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.DefaultSourceSetContainer
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.reflect.Instantiator

class HardwarePluginConvention {
    ProjectInternal project
    final SourceSetContainer sourceSets
    final SourceSetContainer hardwareCompilers

    HardwarePluginConvention(ProjectInternal project, Instantiator instantiator) {
        this.project = project
        sourceSets = instantiator.newInstance(DefaultSourceSetContainer.class, project.fileResolver, project.tasks, instantiator)
		hardwareCompilers = instantiator.newInstance(DefaultHardwareCompilerContainer.class, instantiator)
    }

    def sourceSets(Closure closure) {
        sourceSets.configure(closure)
    }

    def hardwareCompilers(Closure closure) {
        hardwareCompilers.configure(closure)
    }
}
