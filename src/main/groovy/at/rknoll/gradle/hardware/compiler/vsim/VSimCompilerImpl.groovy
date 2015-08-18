package at.rknoll.gradle.hardware.compiler.vsim

import at.rknoll.gradle.hardware.HardwareCompilerImpl
import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwareSourceInformation
import at.rknoll.gradle.hardware.HardwareUtils
import at.rknoll.gradle.hardware.language.verilog.VerilogSourceSet
import at.rknoll.gradle.hardware.language.vhdl.VhdlSourceSet
import at.rknoll.utils.ExecUtils
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class VSimCompilerImpl implements HardwareCompilerImpl {
    private Logger logger
    private String vsimPathVLib
    private String vsimPathVMap
    private Map<String, String> vsimPathCompiler = [:]
    private boolean notFound
    protected Project project

    private enum SourceFileType {
        VHDL,
        VERILOG,
        UNKNOWN
    }

    private class SourceFileInfo {
        public File file
        public SourceFileType type

        public SourceFileInfo(File file) {
            this.file = file
            if (VerilogSourceSet.EXTENSIONS.find { file.name.endsWith(it) } != null) {
                type = SourceFileType.VERILOG
            } else if (VhdlSourceSet.EXTENSIONS.find { file.name.endsWith(it) } != null) {
                type = SourceFileType.VHDL
            } else {
                type = SourceFileType.UNKNOWN
            }
        }
    }

    public VSimCompilerImpl(Project project, String name) {
        this.project = project
        this.logger = LoggerFactory.getLogger(name + '-logger')
        this.notFound = false
    }

    abstract protected VSimExtension getExtension()

    abstract protected Set<String> getPaths()

    private void mapLibrary(String name, String path, File compileDir, VSimExtension extension, Set<String> paths) {
        if (vsimPathVMap == null) vsimPathVMap = VSimUtils.findVSimExecutable("vmap", extension, paths)
        if (vsimPathVMap == null) {
            throw new RuntimeException("Could not find vmap executable in selected compiler");
        }
        def args = [vsimPathVMap, name, path]
        ExecUtils.exec(project, args, compileDir)
    }

    private boolean prepareWork(File file, VSimExtension extension, Set<String> paths) {
        if (vsimPathVLib == null) vsimPathVLib = VSimUtils.findVSimExecutable("vlib", extension, paths)
        if (vsimPathVLib == null) return false

        def compileDir = project.file("compile")
        if (!compileDir.exists()) compileDir.mkdir()

        if (compileDir.isFile()) {
            throw new RuntimeException("Invalid compile directory '" + compileDir.getAbsolutePath() + "'. If this is a File, please remove it.")
        }

        def convention = project.convention.getPlugin HardwarePluginConvention
        HardwareSourceInformation info = null
        convention.hardwareSourceInformation.each {
            if (it.value.containsKey(file)) {
                info = it.value[file]
            }
        }
        def libraryName = HardwareUtils.getLibraryName(info.group, info.name);

        if (!(new File(compileDir, libraryName)).exists()) {
            def args = [vsimPathVLib, libraryName]
            logger.info "creating work library.."
            ExecUtils.exec(project, args, compileDir)
        }

        mapLibrary("work", libraryName, compileDir, extension, paths);
        mapLibrary(libraryName, libraryName, compileDir, extension, paths);

        return true
    }

    private String getCompilerExecutable(SourceFileInfo info) {
        switch (info.type) {
            case SourceFileType.VHDL: return "vcom";
            case SourceFileType.VERILOG: return "vlog";
            default: return null;
        }
    }

    public boolean compile(File file) {
        if (notFound) return false // early exit if we did not find this compiler

        SourceFileInfo info = new SourceFileInfo(file)
        String compiler = getCompilerExecutable(info)
        if (compiler == null) return false

        def extension = getExtension()
        if (extension == null) return false

        def paths = getPaths()
        if (paths == null) return false

        if (!prepareWork(file, extension, paths)) {
            notFound = true
            return false
        }

        if (vsimPathCompiler[compiler] == null) {
            vsimPathCompiler[compiler] = VSimUtils.findVSimExecutable(compiler, extension, paths)
        }

        File compileDir = project.file("compile")
        if (!compileDir.isDirectory()) {
            throw new RuntimeException("Invalid compile directory '" +
                    compileDir.getAbsolutePath() + "'. If this is a File, please remove it.")
        }

        def args = [vsimPathCompiler[compiler], file.getAbsolutePath()]
        ExecUtils.exec(project, args, compileDir)
        return true
    }

}
