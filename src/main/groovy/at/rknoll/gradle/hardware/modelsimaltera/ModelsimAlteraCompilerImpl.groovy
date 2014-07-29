package at.rknoll.gradle.hardware.modelsimaltera

import at.rknoll.gradle.hardware.HardwareCompilerImpl
import java.io.File
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.process.ExecResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ModelsimAlteraCompilerImpl implements HardwareCompilerImpl {
	private Project project
    protected Logger logger

	public ModelsimAlteraCompilerImpl(Project project) {
		this.project = project
        logger = LoggerFactory.getLogger('modelsimaltera-logger')
	}

	public boolean prepareWork() {
		def modelsimAlteraPath = ModelsimAlteraUtils.findModelsimAlteraExecutable("vlib", project.modelsimaltera as ModelsimAlteraExtension)
		File compileDir = project.file("compile")
		if (compileDir.isFile()) {
			throw new RuntimeException("Invalid compile directory '" + compileDir.getAbsolutePath() + "'. If this is a File, please remove it.")
		}
		if (!compileDir.exists()) compileDir.mkdir()
		
        def args = [modelsimAlteraPath, "work"]

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

            logger.print("ModelsimAltera output:\n" + output)
        }
	}
	
	public boolean compile(File file) {
		
		println "-- ModelsimAltera Compile --"
		
		prepareWork();
		
		def modelsimAlteraPath = ModelsimAlteraUtils.findModelsimAlteraExecutable("vcom", project.modelsimaltera as ModelsimAlteraExtension)
		
		File compileDir = project.file("compile")
		if (!compileDir.isDirectory()) {
			throw new RuntimeException("Invalid compile directory '" + compileDir.getAbsolutePath() + "'. If this is a File, please remove it.")
		}
		
        def args = [modelsimAlteraPath, file.getAbsolutePath()]

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

            logger.print("ModelsimAltera output:\n" + output)
        }
	}

}
