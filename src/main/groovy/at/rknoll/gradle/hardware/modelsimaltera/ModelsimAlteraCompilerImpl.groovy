package at.rknoll.gradle.hardware.modelsimaltera

import at.rknoll.gradle.hardware.DefaultHardwareCompiler
import at.rknoll.gradle.hardware.HardwareCompiler
import at.rknoll.gradle.hardware.HardwareUtils
import at.rknoll.gradle.hardware.verilog.VerilogSourceSet
import at.rknoll.gradle.hardware.vhdl.VhdlSourceSet
import org.gradle.api.Project
import org.gradle.process.ExecResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ModelsimAlteraCompilerImpl implements HardwareCompiler {
	public static final String NAME = "modelsimaltera"

    protected Logger logger
	protected String modelsimAlteraPathVLib
	protected String modelsimAlteraPathVMap
	def modelsimAlteraPathCompiler = [:]
	boolean notFound
	private Project project

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

	public ModelsimAlteraCompilerImpl(Project project) {
		this.project = project
        logger = LoggerFactory.getLogger('modelsimaltera-logger')
		notFound = false
	}

	private void mapLibrary(String name, String path, File compileDir) {
		if (modelsimAlteraPathVMap == null) modelsimAlteraPathVMap = ModelsimAlteraUtils.findModelsimAlteraExecutable("vmap", project.modelsimaltera as ModelsimAlteraExtension)
        def args = [modelsimAlteraPathVMap, name, path]
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
		if (modelsimAlteraPathVLib == null) modelsimAlteraPathVLib = ModelsimAlteraUtils.findModelsimAlteraExecutable("vlib", project.modelsimaltera as ModelsimAlteraExtension)
		if (modelsimAlteraPathVLib == null) {
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
			def args = [modelsimAlteraPathVLib, libraryName]
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

		if (modelsimAlteraPathCompiler[compiler] == null) modelsimAlteraPathCompiler[compiler] = ModelsimAlteraUtils.findModelsimAlteraExecutable(compiler, project.modelsimaltera as ModelsimAlteraExtension)

		File compileDir = project.file("compile")
		if (!compileDir.isDirectory()) {
			throw new RuntimeException("Invalid compile directory '" + compileDir.getAbsolutePath() + "'. If this is a File, please remove it.")
		}

        def args = [modelsimAlteraPathCompiler[compiler], file.getAbsolutePath()]

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
