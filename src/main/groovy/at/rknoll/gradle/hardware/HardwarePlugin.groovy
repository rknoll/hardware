package at.rknoll.gradle.hardware

import org.gradle.api.*
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.reflect.Instantiator
import javax.inject.Inject
import org.gradle.api.plugins.BasePlugin

/**
 * Created by rknoll on 20/07/14.
 */
class HardwarePlugin implements Plugin<Project> {
	private final Instantiator instantiator;
	public static final String BUILD_TASK_NAME = "build";

	@Inject
	public HardwarePlugin(Instantiator instantiator) {
		this.instantiator = instantiator;
	}

	public void apply(Project project) {
		HardwarePluginConvention hardwareConvention = new HardwarePluginConvention((ProjectInternal) project, instantiator);
        project.getConvention().getPlugins().put("hardware", hardwareConvention);

		DefaultTask buildTask = project.getTasks().create(BUILD_TASK_NAME, DefaultTask.class);
        buildTask.setDescription("Builds this project.");
        buildTask.setGroup(BasePlugin.BUILD_GROUP);
	}
}
