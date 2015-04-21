package at.rknoll.gradle.hardware.compiler.quartus.tasks

import at.rknoll.gradle.hardware.compiler.quartus.QuartusExtension
import at.rknoll.gradle.hardware.compiler.quartus.QuartusUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.process.ExecResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by Richard on 30.03.2015.
 */
abstract class BaseQuartusTask extends DefaultTask {
    protected Logger logger
    private final String app;

    BaseQuartusTask(String app) {
        this.app = app;
    }

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

            //logger.print("quartus output:\n" + output)
        }
    }

    protected def getArgs() {
        return null
    }

    @TaskAction
    def task() {
        logger = LoggerFactory.getLogger('quartus-logger')

        def synDir = project.file("syn")
        if (!synDir.exists()) synDir.mkdir()
        if (synDir.isFile()) {
            throw new RuntimeException("Invalid synthesize directory '" + synDir.getAbsolutePath() + "'. If this is a File, please remove it.")
        }

        def cmd = QuartusUtils.findQuartusExecutable(app, project.quartus as QuartusExtension)
        def toplevel = project.toplevel.name

        if (toplevel == null) {
            println "toplevel.name not set, using project name '" + project.name + "' as top level entity"
            toplevel = project.name
        }

        def args = [cmd, toplevel, getArgs()].flatten()
        args.removeAll([null])
        runQuartus(args)
    }
}
