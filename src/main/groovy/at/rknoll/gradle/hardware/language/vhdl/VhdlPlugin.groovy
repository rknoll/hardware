package at.rknoll.gradle.hardware.language.vhdl

import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.HardwarePluginConvention
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

import javax.inject.Inject

/**
 * Created by rknoll on 20/07/14.
 */
class VhdlPlugin implements Plugin<Project> {
    private final FileResolver fileResolver

    @Inject
    public VhdlPlugin(FileResolver fileResolver) {
        this.fileResolver = fileResolver
    }

    public void apply(Project project) {
        HardwarePluginConvention hardwareConvention = project.getConvention().getPlugin(HardwarePluginConvention.class)
        SourceSetContainer container = hardwareConvention.getSourceSets()

        container.all(new Action<SourceSet>() {
            public void execute(SourceSet sourceSet) {
                final DefaultVhdlSourceSet vhdlSourceSet = new DefaultVhdlSourceSet(((DefaultSourceSet) sourceSet).getDisplayName(), fileResolver);
                new DslObject(sourceSet).getConvention().getPlugins().put("vhdl", vhdlSourceSet);

                vhdlSourceSet.getVhdl().srcDir(String.format("src/%s/vhdl", sourceSet.getName()));
                sourceSet.getAllSource().source(vhdlSourceSet.getVhdl());

                String prepareTaskName = "prepare" + sourceSet.getName().toLowerCase().capitalize() + "VhdlCompile";
                VhdlPrepareCompileTask prepare = project.getTasks().create(prepareTaskName, VhdlPrepareCompileTask.class);
                prepare.setDescription(String.format("Prepares to Compile the %s Vhdl source.", sourceSet.getName()));
                prepare.setSource(vhdlSourceSet.getVhdl());
                prepare.setGroup(HardwarePlugin.PREPARE_GROUP_NAME);
                project.getTasks().getByName(HardwarePlugin.PREPARE_TASK_NAME).dependsOn(prepareTaskName);

                String dependenciesTaskName = "find" + sourceSet.getName().toLowerCase().capitalize() + "VhdlDependencies";
                VhdlFindDependenciesTask dependencies = project.getTasks().create(dependenciesTaskName, VhdlFindDependenciesTask.class);
                dependencies.setDescription(String.format("Finds dependencies of the %s Vhdl source.", sourceSet.getName()));
                dependencies.setGroup(HardwarePlugin.DEPENDENCIES_GROUP_NAME);
                project.getTasks().getByName(HardwarePlugin.HARDWARE_COMPILE_TASK_NAME).dependsOn(dependenciesTaskName);
                dependencies.dependsOn(HardwarePlugin.PREPARE_TASK_NAME);
            }
        });
    }

}
