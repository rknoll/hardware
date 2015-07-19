package at.rknoll.gradle.hardware.compiler.quartus

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class QuartusSynthesizeTask extends DefaultTask {
    protected Logger logger

    QuartusSynthesizeTask() {
        setDescription("Synthesize this project with Quartus.");
        setGroup(BasePlugin.BUILD_GROUP);
    }

    @TaskAction
    def synthesize() {
        logger = LoggerFactory.getLogger('quartus-logger')
    }

}
