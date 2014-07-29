package at.rknoll.gradle.hardware.vhdl

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
		project.getPlugins().apply(HardwarePlugin.class);
        HardwarePluginConvention hardwareConvention = project.getConvention().getPlugin(HardwarePluginConvention.class)
        SourceSetContainer container = hardwareConvention.getSourceSets()

        // create main and test source sets if not already defined
        if (container.find { SourceSet.MAIN_SOURCE_SET_NAME.equals(it.name) } == null) {
            container.create(SourceSet.MAIN_SOURCE_SET_NAME);
        }

        if (container.find { SourceSet.TEST_SOURCE_SET_NAME.equals(it.name) } == null) {
            container.create(SourceSet.TEST_SOURCE_SET_NAME);
        }

        container.all(new Action<SourceSet>() {
            public void execute(SourceSet sourceSet) {
                final DefaultVhdlSourceSet vhdlSourceSet = new DefaultVhdlSourceSet(((DefaultSourceSet) sourceSet).getDisplayName(), fileResolver);
                new DslObject(sourceSet).getConvention().getPlugins().put("vhdl", vhdlSourceSet);

                vhdlSourceSet.getVhdl().srcDir(String.format("src/%s/vhdl", sourceSet.getName()));

                String compileTaskName = sourceSet.getCompileTaskName("vhdl");
                VhdlCompileTask compile = project.getTasks().create(compileTaskName, VhdlCompileTask.class);
                compile.setDescription(String.format("Compiles the %s Vhdl source.", sourceSet.getName()));
                compile.setSource(vhdlSourceSet.getVhdl());
                project.getTasks().getByName(HardwarePlugin.BUILD_TASK_NAME).dependsOn(compileTaskName);
            }
        });
    }

}
