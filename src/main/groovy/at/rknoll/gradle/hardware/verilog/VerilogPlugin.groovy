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
				final DefaultVerilogSourceSet verilogSourceSet = new DefaultVerilogSourceSet(((DefaultSourceSet) sourceSet).getDisplayName(), fileResolver);
                new DslObject(sourceSet).getConvention().getPlugins().put("verilog", verilogSourceSet);

                verilogSourceSet.getVerilog().srcDir(String.format("src/%s/verilog", sourceSet.getName()));

                String compileTaskName = sourceSet.getCompileTaskName("verilog");
                VerilogCompileTask compile = project.getTasks().create(compileTaskName, VerilogCompileTask.class);
                compile.setDescription(String.format("Compiles the %s Verilog source.", sourceSet.getName()));
                compile.setSource(verilogSourceSet.getVerilog());
            }
        });
	}

}
