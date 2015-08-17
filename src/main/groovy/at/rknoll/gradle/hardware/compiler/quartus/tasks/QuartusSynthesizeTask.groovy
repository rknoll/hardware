package at.rknoll.gradle.hardware.compiler.quartus.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskAction

class QuartusSynthesizeTask extends DefaultTask {
    public final static String SYNTHESIZE_DIR = "syn"
    public final static String TASK_NAME = "synthesize"

    QuartusSynthesizeTask() {
        setDescription("Synthesize this project with Quartus.");
        setGroup(BasePlugin.BUILD_GROUP);
    }

    @TaskAction
    def synthesize() {
    }

}
