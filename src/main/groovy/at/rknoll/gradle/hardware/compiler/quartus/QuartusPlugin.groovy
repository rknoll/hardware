package at.rknoll.gradle.hardware.compiler.quartus

import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.compiler.quartus.tasks.*
import at.rknoll.gradle.hardware.toplevel.TopLevelExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class QuartusPlugin implements Plugin<Project> {
    public static final String NAME = "quartus"
    public static final String SYNTHESIZE_GROUP = "synthesize";

    def void apply(Project project) {
        project.plugins.apply HardwarePlugin

        project.extensions.create NAME, QuartusExtension

        if (project.extensions.findByType(TopLevelExtension) == null) {
            project.extensions.create "toplevel", TopLevelExtension
        }

        def map = project.tasks.create QuartusMapTask.TASK_NAME, QuartusMapTask
        def fit = project.tasks.create QuartusFitTask.TASK_NAME, QuartusFitTask
        def asm = project.tasks.create QuartusAsmTask.TASK_NAME, QuartusAsmTask
        def sta = project.tasks.create QuartusStaTask.TASK_NAME, QuartusStaTask
        def syn = project.tasks.create QuartusSynthesizeTask.TASK_NAME, QuartusSynthesizeTask

        map.dependsOn HardwarePlugin.PREPARE_TASK_NAME
        fit.dependsOn map
        sta.dependsOn fit
        asm.dependsOn fit

        syn.dependsOn sta, asm
        syn.outputs.dir project.file(QuartusSynthesizeTask.SYNTHESIZE_DIR)
        syn.outputs.upToDateWhen { false }

        project.tasks.clean.dependsOn 'cleanSynthesize'
    }

}
