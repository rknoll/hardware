package at.rknoll.gradle.hardware

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.DefaultSourceSetContainer
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.reflect.Instantiator
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class HardwarePluginConvention {
    ProjectInternal project
    final SourceSetContainer sourceSets
    final NamedDomainObjectContainer<HardwareCompiler> hardwareCompilers
    final DefaultDirectedGraph<File, DefaultEdge> hardwareSources
    final Map<File, HardwareSourceInformation> hardwareSourceInformation

    HardwarePluginConvention(ProjectInternal project, Instantiator instantiator, NamedDomainObjectContainer<HardwareCompiler> compilers) {
        this.project = project
        sourceSets = instantiator.newInstance(DefaultSourceSetContainer, project.fileResolver, project.tasks, instantiator)
        hardwareCompilers = compilers
        hardwareSources = new DefaultDirectedGraph<File, DefaultEdge>(DefaultEdge)
        hardwareSourceInformation = new HashMap<>()

        // create main and test source sets if not already defined
        sourceSets.maybeCreate SourceSet.MAIN_SOURCE_SET_NAME
        sourceSets.maybeCreate SourceSet.TEST_SOURCE_SET_NAME
    }

    def sourceSets(Closure closure) {
        sourceSets.configure closure
    }

    def hardwareCompilers(Closure closure) {
        hardwareCompilers.configure closure
    }
}
