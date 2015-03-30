package at.rknoll.gradle.hardware.verilog

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
class VerilogPlugin implements Plugin<Project> {
	private final FileResolver fileResolver

	@Inject
	public VerilogPlugin(FileResolver fileResolver) {
		this.fileResolver = fileResolver
	}

	public void apply(Project project) {
        HardwarePluginConvention hardwareConvention = project.getConvention().getPlugin(HardwarePluginConvention.class)
        SourceSetContainer container = hardwareConvention.getSourceSets()

        container.all(new Action<SourceSet>() {
			public void execute(SourceSet sourceSet) {
				final DefaultVerilogSourceSet verilogSourceSet = new DefaultVerilogSourceSet(((DefaultSourceSet) sourceSet).getDisplayName(), fileResolver);
                new DslObject(sourceSet).getConvention().getPlugins().put("verilog", verilogSourceSet);

                verilogSourceSet.getVerilog().srcDir(String.format("src/%s/verilog", sourceSet.getName()));
				sourceSet.getAllSource().source(verilogSourceSet.getVerilog());

                String prepareTaskName = "prepare" + sourceSet.getName().toLowerCase().capitalize() + "VerilogCompile";
                VerilogPrepareCompileTask prepare = project.getTasks().create(prepareTaskName, VerilogPrepareCompileTask.class);
                prepare.setDescription(String.format("Prepares to Compile the %s Verilog source.", sourceSet.getName()));
                prepare.setSource(verilogSourceSet.getVerilog());
				prepare.setGroup(HardwarePlugin.PREPARE_GROUP_NAME);
				project.getTasks().getByName(HardwarePlugin.PREPARE_TASK_NAME).dependsOn(prepareTaskName);

				String dependenciesTaskName = "find" + sourceSet.getName().toLowerCase().capitalize() + "VerilogDependencies";
                VerilogFindDependenciesTask dependencies = project.getTasks().create(dependenciesTaskName, VerilogFindDependenciesTask.class);
                dependencies.setDescription(String.format("Finds dependencies of the %s Verilog source.", sourceSet.getName()));
				dependencies.setGroup(HardwarePlugin.DEPENDENCIES_GROUP_NAME);
                project.getTasks().getByName(HardwarePlugin.HARDWARE_COMPILE_TASK_NAME).dependsOn(dependenciesTaskName);
                dependencies.dependsOn(HardwarePlugin.PREPARE_TASK_NAME);
			}
        });
	}

}
