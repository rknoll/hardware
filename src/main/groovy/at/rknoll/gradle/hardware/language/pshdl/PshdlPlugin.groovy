package at.rknoll.gradle.hardware.language.pshdl

import at.rknoll.gradle.hardware.HardwarePlugin
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.HasConvention
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.tasks.SourceSet

import javax.inject.Inject

/**
 * Created by rknoll on 20/07/14.
 */
class PshdlPlugin implements Plugin<Project> {
    private final FileResolver fileResolver

    @Inject
    public PshdlPlugin(FileResolver fileResolver) {
        this.fileResolver = fileResolver
    }

    public void apply(Project project) {
        project.plugins.apply HardwarePlugin.class

        project.extensions.pshdl = new PshdlExtension()

        if (project.hardwareCompilers.find { "pshdl".equals(it.name) } == null) {
            project.hardwareCompilers.create("pshdl", {
                it.updateOrder(project.hardwareCompilers)
                it.setDescription("pshdl dummy compiler")
                it.setHardwareCompilerImpl(new PshdlDummyCompilerImpl())
            });
        }

        project.sourceSets.all([execute: { SourceSet sourceSet ->
            def pshdlSourceSet = new DefaultPshdlSourceSet((sourceSet as DefaultSourceSet).displayName, fileResolver)
            (sourceSet as HasConvention).convention.plugins.put "pshdl", pshdlSourceSet

            pshdlSourceSet.pshdl.srcDir String.format("src/%s/pshdl", sourceSet.name)
            sourceSet.allSource.source pshdlSourceSet.pshdl

            String prepareTaskName = "prepare" + sourceSet.name.toLowerCase().capitalize() + "PshdlCompile"
            String cleanPrepareTaskName = "cleanPrepare" + sourceSet.name.toLowerCase().capitalize() + "PshdlCompile"
            project.tasks.create(prepareTaskName, PshdlPrepareCompileTask.class) {
                it.setDescription String.format("Prepares to Compile the %s Pshdl source.", sourceSet.name)
                it.setGroup HardwarePlugin.PREPARE_GROUP_NAME
                it.setSource pshdlSourceSet.pshdl
                it.outputs.dir new File(project.projectDir, "generated/")
                it.outputs.upToDateWhen { false }
            }

            project.tasks.getByName(HardwarePlugin.PREPARE_TASK_NAME).dependsOn prepareTaskName
            project.tasks.clean.dependsOn cleanPrepareTaskName
        }] as Action<SourceSet>)

        project.afterEvaluate {
            project.repositories {
                maven { url project.pshdl.mavenUrl }
            }
            project.configurations {
                pshdl
            }
            project.dependencies {
                pshdl group: 'org.pshdl', name: 'commandline', version: project.pshdl.version
            }
        }
    }

}
