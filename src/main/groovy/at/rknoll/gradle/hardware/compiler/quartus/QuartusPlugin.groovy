package at.rknoll.gradle.hardware.compiler.quartus

import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.compiler.quartus.tasks.QuartusAsmTask
import at.rknoll.gradle.hardware.compiler.quartus.tasks.QuartusFitTask
import at.rknoll.gradle.hardware.compiler.quartus.tasks.QuartusMapTask
import at.rknoll.gradle.hardware.compiler.quartus.tasks.QuartusStaTask
import at.rknoll.gradle.hardware.language.soc.HardwareUiTask
import at.rknoll.gradle.hardware.toplevel.TopLevelExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class QuartusPlugin implements Plugin<Project> {
    public static final String SYNTHESIZE_TASK_NAME = "synthesize";
    public static final String SYNTHESIZE_GROUP = "synthesize";

    def void apply(Project project) {
        project.getPlugins().apply(HardwarePlugin.class);

        project.extensions.quartus = new QuartusExtension()

        if (project.extensions.findByName("toplevel") == null) {
            project.extensions.toplevel = new TopLevelExtension()
        }

        DefaultTask map = project.tasks.create(QuartusMapTask.TASK_NAME, QuartusMapTask.class)
        DefaultTask fit = project.tasks.create(QuartusFitTask.TASK_NAME, QuartusFitTask.class)
        DefaultTask asm = project.tasks.create(QuartusAsmTask.TASK_NAME, QuartusAsmTask.class)
        DefaultTask sta = project.tasks.create(QuartusStaTask.TASK_NAME, QuartusStaTask.class)
        DefaultTask syn = project.tasks.create(SYNTHESIZE_TASK_NAME, QuartusSynthesizeTask.class);

        map.dependsOn HardwarePlugin.PREPARE_TASK_NAME
        fit.dependsOn map
        sta.dependsOn fit
        asm.dependsOn fit

        syn.dependsOn sta, asm
        syn.outputs.dir project.file("syn")
        syn.outputs.upToDateWhen { false }

        project.tasks.clean.dependsOn('cleanSynthesize')

        project.tasks.create(HardwareUiTask.HARDWARE_UI_TASK_NAME, HardwareUiTask.class);
    }

}
