package at.rknoll.gradle.hardware.compiler.questasim

import at.rknoll.gradle.hardware.compiler.vsim.VSimCompilerImpl
import at.rknoll.gradle.hardware.compiler.vsim.VSimExtension
import org.gradle.api.Project

class QuestasimCompilerImpl extends VSimCompilerImpl {
    public QuestasimCompilerImpl(Project project) {
        super(project, QuestasimPlugin.NAME)
    }

    @Override
    protected VSimExtension getExtension() {
        return project.extensions.getByType(QuestasimExtension)
    }

    @Override
    protected Set<String> getPaths() {
        return ["questasim", "win32", "questa"] as Set
    }
}
