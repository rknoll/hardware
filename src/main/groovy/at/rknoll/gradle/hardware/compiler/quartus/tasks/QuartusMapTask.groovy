package at.rknoll.gradle.hardware.compiler.quartus.tasks

import at.rknoll.gradle.hardware.compiler.quartus.QuartusPlugin
import at.rknoll.utils.FileUtils
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator

/**
 * Created by Richard on 30.03.2015.
 */
class QuartusMapTask extends BaseQuartusTask {
    public final static String TASK_NAME = "quartus_map"

    QuartusMapTask() {
        super("quartus_map")
        setDescription "Analysis and Synthesis with Quartus."
        setGroup QuartusPlugin.SYNTHESIZE_GROUP
    }

    @Override
    protected def getArgs() {
        TopologicalOrderIterator<File, DefaultEdge> orderIterator
        orderIterator = new TopologicalOrderIterator<File, DefaultEdge>(project.hardwareSources)
        File base = project.file('syn')

        def sources = []
        while (orderIterator.hasNext()) {
            def file = orderIterator.next();
            String relative = FileUtils.getRelativePath(file.path, base.path, file.separator)
            sources << "--source=" + relative
        }

        return sources
    }
}
