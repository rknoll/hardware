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
    final Map<String, DefaultDirectedGraph<File, DefaultEdge>> hardwareSources
    final Map<String, Map<File, HardwareSourceInformation>> hardwareSourceInformation
    final Map<File, List<String>> hardwareSourceDependencies
    final Map<File, List<String>> hardwareSourceDefinitions

    HardwarePluginConvention(ProjectInternal project, Instantiator instantiator, NamedDomainObjectContainer<HardwareCompiler> compilers) {
        this.project = project
        sourceSets = instantiator.newInstance(DefaultSourceSetContainer, project.fileResolver, project.tasks, instantiator)
        hardwareCompilers = compilers
        hardwareSources = new HashMap<>()
        hardwareSourceInformation = new HashMap<>()
        hardwareSourceDependencies = new HashMap<>()
        hardwareSourceDefinitions = new HashMap<>()

        // create main and test source sets if not already defined
        [SourceSet.MAIN_SOURCE_SET_NAME, SourceSet.TEST_SOURCE_SET_NAME].each {
            sourceSets.maybeCreate it
            // create empty graphs
            hardwareSources.put it, new DefaultDirectedGraph<>(DefaultEdge)
            hardwareSourceInformation.put it, new HashMap<>()
        }
    }

    def sourceSets(Closure closure) {
        sourceSets.configure closure
    }

    def hardwareCompilers(Closure closure) {
        hardwareCompilers.configure closure
    }
}
