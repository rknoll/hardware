package at.rknoll.gradle.hardware

import at.rknoll.gradle.hardware.language.pshdl.PshdlPlugin
import at.rknoll.gradle.hardware.language.verilog.VerilogPlugin
import at.rknoll.gradle.hardware.language.vhdl.VhdlPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Zip
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

/**
 * Created by rknoll on 20/07/14.
 */
class HardwarePlugin implements Plugin<ProjectInternal> {
    private final Instantiator instantiator;
    public static final String PREPARE_TASK_NAME = "prepareHardwareCompile";
    public static final String HARDWARE_COMPILE_TASK_NAME = "hardwareCompile";
    public static final String EXTRACT_DEPS_TASK_NAME = "extractDependencies";
    public static final String PREPARE_GROUP_NAME = "Prepare Compile";
    public static final String DEPENDENCIES_GROUP_NAME = "Dependencies";

    @Inject
    public HardwarePlugin(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    public void apply(ProjectInternal project) {
        project.getPlugins().apply(BasePlugin.class);
        project.getPlugins().apply(MavenPlugin.class);

        def compilers = project.container(HardwareCompiler)

        HardwarePluginConvention hardwareConvention = new HardwarePluginConvention(project, instantiator, compilers);
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
            runtime.extendsFrom(compile)
            testCompile.extendsFrom(compile)
            testRuntime.extendsFrom(runtime, testCompile)
            delegate.default.extendsFrom(runtime)
        }

        HardwarePrepareCompileTask extractDependencies = project.getTasks().create(EXTRACT_DEPS_TASK_NAME, HardwarePrepareCompileTask.class);
        extractDependencies.setDescription("Extracts all dependencies of this project.");
        extractDependencies.setGroup(PREPARE_GROUP_NAME);
        extractDependencies.dependsOn(project.configurations.compile);
        extractDependencies.outputs.dir new File(project.projectDir, "libs")
        extractDependencies.outputs.upToDateWhen { false }
        project.tasks.clean.dependsOn('cleanExtractDependencies')

        DefaultTask prepareTask = project.getTasks().create(PREPARE_TASK_NAME, DefaultTask.class);
        prepareTask.setDescription("Prepares to Compile this project.");
        prepareTask.setGroup(PREPARE_GROUP_NAME);
        prepareTask.dependsOn(EXTRACT_DEPS_TASK_NAME);

        HardwareCompileTask compile = project.getTasks().create(HARDWARE_COMPILE_TASK_NAME, HardwareCompileTask.class);
        compile.setDescription("Compile Hardware.");
        compile.setGroup(BasePlugin.BUILD_GROUP);
        compile.setSource(project.sourceSets.main.getAllSource());
        compile.dependsOn(PREPARE_TASK_NAME);
        compile.outputs.dir new File(project.projectDir, "graph")
        compile.outputs.dir new File(project.projectDir, "compile")
        compile.outputs.upToDateWhen { false }
        project.tasks.clean.dependsOn('cleanHardwareCompile')

        if (project.tasks.findByName("build") == null) {
            DefaultTask build = project.getTasks().create("build", DefaultTask.class);
            build.setDescription("Builds this project.");
            build.setGroup(BasePlugin.BUILD_GROUP);
        }

        project.tasks.build.dependsOn compile

        Task zipTask = project.getTasks().create("sources", Zip.class);
        zipTask.setDescription("Zips all sources of this project.");
        zipTask.setGroup(DEPENDENCIES_GROUP_NAME);
        zipTask.dependsOn(PREPARE_TASK_NAME);
        zipTask.from project.sourceSets.main.allSource

        project.artifacts {
            runtime zipTask
        }

        // apply all supported language plugins
        project.getPlugins().apply(VhdlPlugin.class);
        project.getPlugins().apply(VerilogPlugin.class);
        project.getPlugins().apply(PshdlPlugin.class);
    }
}
