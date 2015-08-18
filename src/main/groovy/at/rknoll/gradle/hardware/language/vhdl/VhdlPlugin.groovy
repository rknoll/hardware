package at.rknoll.gradle.hardware.language.vhdl

import at.rknoll.gradle.hardware.HardwarePlugin
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.HasConvention
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.tasks.SourceSet

import javax.inject.Inject

class VhdlPlugin implements Plugin<Project> {
    private final FileResolver fileResolver

    @Inject
    public VhdlPlugin(FileResolver fileResolver) {
        this.fileResolver = fileResolver
    }

    public void apply(Project project) {
        project.plugins.apply HardwarePlugin

        project.sourceSets.all([execute: { SourceSet sourceSet ->
            def vhdlSourceSet = new DefaultVhdlSourceSet((sourceSet as DefaultSourceSet).displayName, fileResolver)
            (sourceSet as HasConvention).convention.plugins.put "vhdl", vhdlSourceSet

            vhdlSourceSet.vhdl.srcDir String.format("src/%s/vhdl", sourceSet.name)
            sourceSet.allSource.source vhdlSourceSet.vhdl

            String prepareTaskName = "prepare" + sourceSet.name.toLowerCase().capitalize() + "VhdlCompile"
            project.tasks.create(prepareTaskName, VhdlPrepareCompileTask) {
                it.sourceSet = sourceSet.name
                it.setDescription String.format("Prepares to Compile the %s Vhdl source.", sourceSet.name)
                it.setSource vhdlSourceSet.vhdl
                it.setGroup HardwarePlugin.PREPARE_GROUP_NAME
            }

            String dependenciesTaskName = "find" + sourceSet.name.toLowerCase().capitalize() + "VhdlDependencies"
            project.tasks.create(dependenciesTaskName, VhdlFindDependenciesTask) {
                it.sourceSet = sourceSet.name
                it.setDescription String.format("Finds dependencies of the %s Vhdl source.", sourceSet.getName())
                it.setGroup HardwarePlugin.DEPENDENCIES_GROUP_NAME
                it.dependsOn HardwarePlugin.PREPARE_TASK_NAME
            }

            project.tasks.getByName(HardwarePlugin.PREPARE_TASK_NAME).dependsOn prepareTaskName
            project.tasks.getByName(HardwarePlugin.HARDWARE_COMPILE_TASK_NAME).dependsOn dependenciesTaskName
        }] as Action<SourceSet>)
    }
}

