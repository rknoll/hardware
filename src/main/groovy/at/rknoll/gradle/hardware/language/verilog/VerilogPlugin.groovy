package at.rknoll.gradle.hardware.language.verilog

import at.rknoll.gradle.hardware.HardwarePlugin
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.HasConvention
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.tasks.SourceSet

import javax.inject.Inject

/**
 * Created by rknoll on 20/07/14.
 */
class VerilogPlugin implements Plugin<Project> {
    private final FileResolver fileResolver

    @Inject
    public VerilogPlugin(FileResolver fileResolver) {
        this.fileResolver = fileResolver
    }

    public void apply(Project project) {
        project.plugins.apply HardwarePlugin.class

        project.sourceSets.all([execute: { SourceSet sourceSet ->
            def verilogSourceSet = new DefaultVerilogSourceSet((sourceSet as DefaultSourceSet).displayName, fileResolver)
            (sourceSet as HasConvention).convention.plugins.put "verilog", verilogSourceSet

            verilogSourceSet.verilog.srcDir String.format("src/%s/verilog", sourceSet.name)
            sourceSet.allSource.source verilogSourceSet.verilog

            String prepareTaskName = "prepare" + sourceSet.name.toLowerCase().capitalize() + "VerilogCompile"
            project.tasks.create(prepareTaskName, VerilogPrepareCompileTask.class) {
                it.setDescription String.format("Prepares to Compile the %s Verilog source.", sourceSet.name)
                it.setSource verilogSourceSet.verilog
                it.setGroup HardwarePlugin.PREPARE_GROUP_NAME
            }

            String dependenciesTaskName = "find" + sourceSet.name.toLowerCase().capitalize() + "VerilogDependencies"
            project.tasks.create(dependenciesTaskName, VerilogFindDependenciesTask.class) {
                it.setDescription String.format("Finds dependencies of the %s Verilog source.", sourceSet.getName())
                it.setGroup HardwarePlugin.DEPENDENCIES_GROUP_NAME
                it.dependsOn HardwarePlugin.PREPARE_TASK_NAME
            }

            project.tasks.getByName(HardwarePlugin.HARDWARE_COMPILE_TASK_NAME).dependsOn dependenciesTaskName
            project.tasks.getByName(HardwarePlugin.PREPARE_TASK_NAME).dependsOn prepareTaskName
        }] as Action<SourceSet>)
    }

}
