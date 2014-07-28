package at.rknoll.gradle.hardware.vhdl

import org.gradle.api.*
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.SourceSet
import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.HardwarePluginConvention
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.internal.file.FileResolver
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

		project.getConvention().getPlugin(HardwarePluginConvention.class).getSourceSets().all(new Action<SourceSet>() {
			public void execute(SourceSet sourceSet) {
				final DefaultVhdlSourceSet vhdlSourceSet = new DefaultVhdlSourceSet(((DefaultSourceSet) sourceSet).getDisplayName(), fileResolver);
                new DslObject(sourceSet).getConvention().getPlugins().put("vhdl", vhdlSourceSet);

                vhdlSourceSet.getVhdl().srcDir(String.format("src/%s/vhdl", sourceSet.getName()));
			}
		});
	}

}
