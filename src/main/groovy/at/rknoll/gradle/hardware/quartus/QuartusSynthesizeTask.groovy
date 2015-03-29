package at.rknoll.gradle.hardware.quartus

import at.rknoll.gradle.hardware.at.rknoll.utils.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.process.ExecResult
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class QuartusSynthesizeTask extends DefaultTask {
    protected Logger logger

    def runQuartus(args) {
        File base = project.file('syn')
        new ByteArrayOutputStream().withStream { os ->
            ExecResult result = project.exec {
                commandLine = args
                standardOutput = os
                ignoreExitValue = true
                workingDir = base
            }

            String output = os.toString()
            int exitCode = result.getExitValue()

            if (exitCode != 0) {
                throw new TaskExecutionException(this, new Exception("Error " + exitCode + " while executing '" + args.join(" ") + "'\noutput:\n" + output));
            }

            logger.print("quartus output:\n" + output)
        }
    }

	@TaskAction
	def synthesize() {
        logger = LoggerFactory.getLogger('quartus-logger')

		println "-- Quartus Synthesize --"

        def quartusMap = QuartusUtils.findQuartusExecutable("quartus_map", project.quartus as QuartusExtension)
        def quartusFit = QuartusUtils.findQuartusExecutable("quartus_fit", project.quartus as QuartusExtension)
        def quartusAsm = QuartusUtils.findQuartusExecutable("quartus_asm", project.quartus as QuartusExtension)
        def quartusSta = QuartusUtils.findQuartusExecutable("quartus_sta", project.quartus as QuartusExtension)

        def toplevel = project.toplevel.name

        if (toplevel == null) {
            println "toplevel.name not set, using project name '" + project.name + "' as top level entity"
            toplevel = project.name
        }

        TopologicalOrderIterator<File, DefaultEdge> orderIterator
        orderIterator = new TopologicalOrderIterator<File, DefaultEdge>(project.hardwareSources)
        File base = project.file('syn')

        def sources = []
        while (orderIterator.hasNext()) {
            def file = orderIterator.next();
            String relative = FileUtils.getRelativePath(file.path, base.path, file.separator)
            sources << "--source=" + relative
        }

        runQuartus([quartusMap, toplevel, sources].flatten())
        runQuartus([quartusFit, toplevel])
        runQuartus([quartusAsm, toplevel])
        runQuartus([quartusSta, toplevel])
	}

}
