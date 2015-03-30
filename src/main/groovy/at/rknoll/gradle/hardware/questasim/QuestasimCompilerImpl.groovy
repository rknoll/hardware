package at.rknoll.gradle.hardware.questasim

import at.rknoll.gradle.hardware.HardwareCompilerContainer
import at.rknoll.gradle.hardware.HardwareCompilerImpl
import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwareUtils
import at.rknoll.gradle.hardware.verilog.VerilogSourceSet
import at.rknoll.gradle.hardware.vhdl.VhdlSourceSet
import at.rknoll.gradle.hardware.vhdl.VhdlUtils
import org.gradle.api.Project
import org.gradle.process.ExecResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class QuestasimCompilerImpl implements HardwareCompilerImpl {
	public static final String NAME = "questasim"

	private Project project
    protected Logger logger
	protected String questasimPathVLib
	protected String questasimPathVMap
	def questasimPathCompiler = [:]
	boolean notFound

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

	public QuestasimCompilerImpl(Project project) {
		this.project = project
        logger = LoggerFactory.getLogger('questasim-logger')
		notFound = false
	}

	private void mapLibrary(String name, String path, File compileDir) {
		if (questasimPathVMap == null) questasimPathVMap = QuestasimUtils.findQuestasimExecutable("vmap", project.questasim as QuestasimExtension)
        def args = [questasimPathVMap, name, path]
        new ByteArrayOutputStream().withStream { os ->
            ExecResult result = project.exec {
				workingDir = compileDir.getAbsolutePath()
                commandLine = args
                standardOutput = os
                ignoreExitValue = true
            }

            String output = os.toString()
            int exitCode = result.getExitValue()

            if (exitCode != 0) {
                throw new RuntimeException("Error " + exitCode + " while executing '" + args.join(" ") + "'\noutput:\n" + output);
            }
        }
	}

	public boolean prepareWork(File file) {
		if (questasimPathVLib == null) questasimPathVLib = QuestasimUtils.findQuestasimExecutable("vlib", project.questasim as QuestasimExtension)
		if (questasimPathVLib == null) {
			notFound = true
			return false
		}

		def compileDir = project.file("compile")
		if (!compileDir.exists()) compileDir.mkdir()
		if (compileDir.isFile()) {
			throw new RuntimeException("Invalid compile directory '" + compileDir.getAbsolutePath() + "'. If this is a File, please remove it.")
		}

		def info = project.hardwareSourceInformation[file]
		def libraryName = HardwareUtils.getLibraryName(info.group, info.name);

		if (!(new File(compileDir, libraryName)).exists()) {
			def args = [questasimPathVLib, libraryName]
			println "creating work library.."

			new ByteArrayOutputStream().withStream { os ->
				ExecResult result = project.exec {
					workingDir = compileDir.getAbsolutePath()
					commandLine = args
					standardOutput = os
					ignoreExitValue = true
				}

				String output = os.toString()
				int exitCode = result.getExitValue()

				if (exitCode != 0) {
					throw new RuntimeException("Error " + exitCode + " while executing '" + args.join(" ") + "'\noutput:\n" + output);
				}
			}
		}

		mapLibrary("work", libraryName, compileDir);
		mapLibrary(libraryName, libraryName, compileDir);

		return true
	}

	public boolean compile(File file) {
		if (notFound) return false

		SourceFileInfo info = new SourceFileInfo(file)
		if (info.type == SourceFileType.UNKNOWN) return false

		String compiler = null

		switch (info.type) {
			case SourceFileType.VHDL: compiler = "vcom"; break;
			case SourceFileType.VERILOG: compiler = "vlog"; break;
		}

		if (compiler == null) return false

		if (!prepareWork(file)) return false

		if (questasimPathCompiler[compiler] == null) questasimPathCompiler[compiler] = QuestasimUtils.findQuestasimExecutable(compiler, project.questasim as QuestasimExtension)

		File compileDir = project.file("compile")
		if (!compileDir.isDirectory()) {
			throw new RuntimeException("Invalid compile directory '" + compileDir.getAbsolutePath() + "'. If this is a File, please remove it.")
		}

        def args = [questasimPathCompiler[compiler], file.getAbsolutePath()]

        new ByteArrayOutputStream().withStream { os ->
            ExecResult result = project.exec {
				workingDir = compileDir.getAbsolutePath()
                commandLine = args
                standardOutput = os
                ignoreExitValue = true
            }

            String output = os.toString()
            int exitCode = result.getExitValue()

            if (exitCode != 0) {
                throw new RuntimeException("Error " + exitCode + " while executing '" + args.join(" ") + "'\noutput:\n" + output);
            }
        }

		return true
	}

}
