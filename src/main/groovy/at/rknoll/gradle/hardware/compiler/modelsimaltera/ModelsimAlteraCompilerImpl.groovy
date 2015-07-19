package at.rknoll.gradle.hardware.compiler.modelsimaltera

import at.rknoll.gradle.hardware.compiler.vsim.VSimCompilerImpl
import at.rknoll.gradle.hardware.compiler.vsim.VSimExtension
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class ModelsimAlteraCompilerImpl extends VSimCompilerImpl {
    public static final String NAME = "modelsimaltera"

    private static final Set<String> MODELSIM_ALTERA_DIRS = new HashSet<String>(Arrays.asList(
            ["altera", "modelsim_ase", "win32aloem"] as String[]
    ));

    public ModelsimAlteraCompilerImpl(Project project) {
        super(project, LoggerFactory.getLogger('modelsimaltera-logger'))
    }

    @Override
    protected VSimExtension getExtension() {
        return project.modelsimaltera
    }

    @Override
    protected Set<String> getPaths() {
        return MODELSIM_ALTERA_DIRS
    }
}
