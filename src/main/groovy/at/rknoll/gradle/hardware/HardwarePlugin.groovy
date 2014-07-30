package at.rknoll.gradle.hardware

import org.gradle.api.*
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.reflect.Instantiator
import javax.inject.Inject
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * Created by rknoll on 20/07/14.
 */
class HardwarePlugin implements Plugin<Project> {
	private final Instantiator instantiator;
	public static final String PREPARE_TASK_NAME = "prepareHardwareCompile";
	public static final String BUILD_TASK_NAME = "build";
	public static final String PREPARE_GROUP_NAME = "Prepare Compile";
	public static final String DEPENDENCIES_GROUP_NAME = "Dependencies";

	@Inject
	public HardwarePlugin(Instantiator instantiator) {
		this.instantiator = instantiator;
	}

	public void apply(Project project) {
		project.getPlugins().apply(BasePlugin.class);
		HardwarePluginConvention hardwareConvention = new HardwarePluginConvention((ProjectInternal) project, instantiator);
        project.getConvention().getPlugins().put("hardware", hardwareConvention);

		SourceSetContainer container = hardwareConvention.getSourceSets()

        // create main and test source sets if not already defined
        if (container.find { SourceSet.MAIN_SOURCE_SET_NAME.equals(it.name) } == null) {
            container.create(SourceSet.MAIN_SOURCE_SET_NAME);
        }

        if (container.find { SourceSet.TEST_SOURCE_SET_NAME.equals(it.name) } == null) {
            container.create(SourceSet.TEST_SOURCE_SET_NAME);
        }

		project.configurations {
			compile
		}

		DefaultTask prepareTask = project.getTasks().create(PREPARE_TASK_NAME, DefaultTask.class);
		prepareTask.setDescription("Prepares to Compile this project.");
		prepareTask.setGroup(HardwarePlugin.PREPARE_GROUP_NAME);
		prepareTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

		HardwareCompileTask compile = project.getTasks().create(BUILD_TASK_NAME, HardwareCompileTask.class);
        compile.setDescription("Builds this project.");
        compile.setGroup(BasePlugin.BUILD_GROUP);
		compile.setSource(project.sourceSets.main.getAllSource());
		compile.dependsOn(PREPARE_TASK_NAME);
	}
}
