package at.rknoll.gradle.hardware.compiler.quartus.tasks

import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.utils.FileUtils
import org.gradle.api.tasks.SourceSet
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator

class QuartusMapTask extends BaseQuartusTask {
    public final static String TASK_NAME = "quartus_map"

    QuartusMapTask() {
        super(TASK_NAME)
        setDescription "Analysis and Synthesis with Quartus."
    }

    @Override
    protected def getArgs() {
        def convention = project.convention.getPlugin HardwarePluginConvention
        def orderIterator = new TopologicalOrderIterator<File, DefaultEdge>(convention.hardwareSources[SourceSet.MAIN_SOURCE_SET_NAME])
        def base = project.file('syn')

        def sources = []
        while (orderIterator.hasNext()) {
            def file = orderIterator.next();
            def relative = FileUtils.getRelativePath(file.path, base.path, file.separator)
            sources << "--source=" + relative
        }

        return sources
    }
}
