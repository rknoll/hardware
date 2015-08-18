package at.rknoll.gradle.hardware

import at.rknoll.gradle.hardware.language.pshdl.PshdlPlugin
import at.rknoll.gradle.hardware.language.verilog.VerilogPlugin
import at.rknoll.gradle.hardware.language.vhdl.VhdlPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.bundling.Zip
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class HardwarePlugin implements Plugin<ProjectInternal> {
    private final Instantiator instantiator
    public static final String PREPARE_TASK_NAME = "prepareHardwareCompile"
    public static final String HARDWARE_COMPILE_TASK_NAME = "hardwareCompile"
    public static final String EXTRACT_DEPS_TASK_NAME = "extractDependencies"
    public static final String PREPARE_GROUP_NAME = "Prepare Compile"
    public static final String DEPENDENCIES_GROUP_NAME = "Dependencies"

    @Inject
    public HardwarePlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    public void apply(ProjectInternal project) {
        project.plugins.apply BasePlugin
        project.plugins.apply MavenPlugin

        def compilers = project.container(HardwareCompiler)
        def hardwareConvention = new HardwarePluginConvention(project, instantiator, compilers)
        project.convention.plugins.put "hardware", hardwareConvention

        project.configurations {
            compile
            archives
            runtime.extendsFrom compile
            testCompile.extendsFrom compile
            testRuntime.extendsFrom runtime, testCompile
            delegate.default.extendsFrom runtime
        }

        project.tasks.create(EXTRACT_DEPS_TASK_NAME, HardwarePrepareCompileTask) {
            it.setDescription "Extracts all dependencies of this project."
            it.setGroup PREPARE_GROUP_NAME
            it.dependsOn project.configurations.compile
            it.outputs.dir new File(project.projectDir, "libs")
            it.outputs.upToDateWhen { false }
        }

        project.tasks.create(PREPARE_TASK_NAME, DefaultTask) {
            it.setDescription "Prepares to Compile this project."
            it.setGroup PREPARE_GROUP_NAME
            it.dependsOn EXTRACT_DEPS_TASK_NAME
        }

        project.tasks.create(HARDWARE_COMPILE_TASK_NAME, HardwareCompileTask) {
            it.setDescription "Compile Hardware."
            it.setGroup BasePlugin.BUILD_GROUP
            it.dependsOn PREPARE_TASK_NAME
            it.outputs.dir new File(project.projectDir, "graph")
            it.outputs.dir new File(project.projectDir, "compile")
            it.outputs.upToDateWhen { false }
        }

        if (project.tasks.findByName("build") == null) {
            project.tasks.create("build", DefaultTask) {
                it.setDescription "Builds this project."
                it.setGroup BasePlugin.BUILD_GROUP
            }
        }

        def zipTask = project.tasks.create("sources", Zip) {
            it.setDescription "Zips all sources of this project."
            it.setGroup DEPENDENCIES_GROUP_NAME
            it.dependsOn PREPARE_TASK_NAME
            it.from project.sourceSets.main.allSource
        }

        project.artifacts.add "runtime", zipTask

        project.tasks.clean.dependsOn 'cleanExtractDependencies'
        project.tasks.clean.dependsOn 'cleanHardwareCompile'
        project.tasks.build.dependsOn HARDWARE_COMPILE_TASK_NAME

        // apply all supported language plugins
        project.plugins.apply VhdlPlugin
        project.plugins.apply VerilogPlugin
        project.plugins.apply PshdlPlugin
    }
}
