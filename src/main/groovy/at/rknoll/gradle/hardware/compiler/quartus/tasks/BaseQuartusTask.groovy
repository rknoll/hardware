package at.rknoll.gradle.hardware.compiler.quartus.tasks

import at.rknoll.gradle.hardware.compiler.quartus.QuartusExtension
import at.rknoll.gradle.hardware.compiler.quartus.QuartusPlugin
import at.rknoll.gradle.hardware.compiler.quartus.QuartusUtils
import at.rknoll.gradle.hardware.toplevel.TopLevelExtension
import at.rknoll.utils.ExecUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class BaseQuartusTask extends DefaultTask {
    protected Logger logger
    private final String app

    BaseQuartusTask(String app) {
        this.app = app
        setGroup QuartusPlugin.SYNTHESIZE_GROUP
    }

    protected def getArgs() {
        return null
    }

    @TaskAction
    def task() {
        logger = LoggerFactory.getLogger('quartus-logger')

        def synDir = project.file(QuartusSynthesizeTask.SYNTHESIZE_DIR)
        if (!synDir.exists()) synDir.mkdir()
        if (synDir.isFile()) {
            throw new RuntimeException("Invalid synthesize directory '"
                    + synDir.getAbsolutePath() + "'. If this is a File, please remove it.")
        }

        def quartusExtension = project.extensions.getByType QuartusExtension
        def toplevelExtension = project.extensions.getByType TopLevelExtension

        def cmd = QuartusUtils.findQuartusExecutable(app, quartusExtension)

        def toplevel = toplevelExtension != null ? toplevelExtension.name : null
        if (toplevel == null) {
            logger.info "toplevel.name not set, using project name '" + project.name + "' as top level entity"
            toplevel = project.name
        }

        def args = [cmd, toplevel, getArgs()].flatten()
        args.removeAll([null])
        ExecUtils.exec(project, args, synDir)
    }
}
