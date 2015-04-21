package at.rknoll.gradle.hardware.language.pshdl

import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwareCompilerContainer
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
class PshdlPlugin implements Plugin<Project> {
	private final FileResolver fileResolver

	@Inject
	public PshdlPlugin(FileResolver fileResolver) {
		this.fileResolver = fileResolver
	}

	public void apply(Project project) {
        HardwarePluginConvention hardwareConvention = project.getConvention().getPlugin(HardwarePluginConvention.class)

        project.extensions.pshdl = new PshdlExtension()
		HardwareCompilerContainer compilers = hardwareConvention.getHardwareCompilers()

		if (compilers.find { "pshdl".equals(it.name) } == null) {
			compilers.create("pshdl", {
				it.setDescription("pshdl dummy compiler")
				it.setHardwareCompilerImpl(new PshdlDummyCompilerImpl())
			});
		}

        SourceSetContainer container = hardwareConvention.getSourceSets()

        container.all(new Action<SourceSet>() {
            public void execute(SourceSet sourceSet) {
                final DefaultPshdlSourceSet pshdlSourceSet = new DefaultPshdlSourceSet(((DefaultSourceSet) sourceSet).getDisplayName(), fileResolver);
                new DslObject(sourceSet).getConvention().getPlugins().put("pshdl", pshdlSourceSet);

                pshdlSourceSet.getPshdl().srcDir(String.format("src/%s/pshdl", sourceSet.getName()));
				sourceSet.getAllSource().source(pshdlSourceSet.getPshdl());

                String prepareTaskName = "prepare" + sourceSet.getName().toLowerCase().capitalize() + "PshdlCompile";
                String cleanPrepareTaskName = "cleanPrepare" + sourceSet.getName().toLowerCase().capitalize() + "PshdlCompile";
                PshdlPrepareCompileTask prepare = project.getTasks().create(prepareTaskName, PshdlPrepareCompileTask.class);
                prepare.setDescription(String.format("Prepares to Compile the %s Pshdl source.", sourceSet.getName()));
				prepare.setGroup(HardwarePlugin.PREPARE_GROUP_NAME);
				prepare.setSource(pshdlSourceSet.getPshdl());
                project.getTasks().getByName(HardwarePlugin.PREPARE_TASK_NAME).dependsOn(prepareTaskName);
				prepare.outputs.dir new File(project.projectDir, "generated/")
				prepare.outputs.upToDateWhen { false }
				project.tasks.clean.dependsOn(cleanPrepareTaskName)
			}
        });
    }

}
