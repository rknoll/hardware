package at.rknoll.gradle.hardware

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.reflect.Instantiator
import javax.inject.Inject
import org.gradle.api.plugins.BasePlugin

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
			archives
			delegate.default.extendsFrom(archives)
		}

		DefaultTask prepareTask = project.getTasks().create(PREPARE_TASK_NAME, DefaultTask.class);
		prepareTask.setDescription("Prepares to Compile this project.");
		prepareTask.setGroup(HardwarePlugin.PREPARE_GROUP_NAME);
		prepareTask.dependsOn(project.configurations.compile);

		HardwareCompileTask compile = project.getTasks().create(BUILD_TASK_NAME, HardwareCompileTask.class);
        compile.setDescription("Builds this project.");
        compile.setGroup(BasePlugin.BUILD_GROUP);
		compile.setSource(project.sourceSets.main.getAllSource());
		compile.dependsOn(PREPARE_TASK_NAME);

		Task zipTask = project.getTasks().create("sources", Zip.class);
		zipTask.setDescription("Zips all sources of this project.");
		zipTask.setGroup(HardwarePlugin.DEPENDENCIES_GROUP_NAME);
		zipTask.dependsOn(compile);
		zipTask.from project.sourceSets.main.allSource

		project.artifacts {
			archives zipTask
		}
	}
}
