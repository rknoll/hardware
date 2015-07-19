package at.rknoll.gradle.hardware.compiler.questasim

import at.rknoll.gradle.hardware.compiler.vsim.VSimCompilerImpl
import at.rknoll.gradle.hardware.compiler.vsim.VSimExtension
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class QuestasimCompilerImpl extends VSimCompilerImpl {
    public static final String NAME = "questasim"

    private static final Set<String> QUESTASIM_DIRS = new HashSet<String>(Arrays.asList(
            ["questasim", "win32", "questa"] as String[]
    ));

    public QuestasimCompilerImpl(Project project) {
        super(project, LoggerFactory.getLogger('questasim-logger'))
    }

    @Override
    protected VSimExtension getExtension() {
        return project.questasim
    }

    @Override
    protected Set<String> getPaths() {
        return QUESTASIM_DIRS
    }
}
