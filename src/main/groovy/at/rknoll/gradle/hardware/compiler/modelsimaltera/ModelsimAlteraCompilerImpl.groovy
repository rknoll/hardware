package at.rknoll.gradle.hardware.compiler.modelsimaltera

import at.rknoll.gradle.hardware.compiler.vsim.VSimCompilerImpl
import at.rknoll.gradle.hardware.compiler.vsim.VSimExtension
import org.gradle.api.Project

class ModelsimAlteraCompilerImpl extends VSimCompilerImpl {
    public ModelsimAlteraCompilerImpl(Project project) {
        super(project, ModelsimAlteraPlugin.NAME)
    }

    @Override
    protected VSimExtension getExtension() {
        return project.extensions.getByType(ModelsimAlteraExtension)
    }

    @Override
    protected Set<String> getPaths() {
        return ["altera", "modelsim_ase", "win32aloem"] as Set
    }
}
