package at.rknoll.gradle.hardware.quartus

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.process.ExecResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class QuartusSynthesizeTask extends DefaultTask {
    protected Logger logger

	@TaskAction
	def synthesize() {
        logger = LoggerFactory.getLogger('quartus-logger')

		println "-- Quartus Synthesize --"
		def quartusPath = QuartusUtils.findQuartusExecutable("quartus_sh", project.quartus as QuartusExtension)
        //quartus_sh --flow compile

        def args = [quartusPath]

        new ByteArrayOutputStream().withStream { os ->
            ExecResult result = project.exec {
                commandLine = args
                standardOutput = os
                ignoreExitValue = true
            }

            String output = os.toString()
            int exitCode = result.getExitValue()

            if (exitCode != 0) {
                throw new TaskExecutionException(this, new Exception("Error " + exitCode + " while executing '" + args.join(" ") + "'\noutput:\n" + output));
            }

            logger.print("quartus output:\n" + output)
        }

	}

}
