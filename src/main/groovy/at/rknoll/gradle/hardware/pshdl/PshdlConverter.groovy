package at.rknoll.gradle.hardware.pshdl

import at.rknoll.gradle.hardware.HardwareCompilerImpl
import at.rknoll.gradle.hardware.verilog.VerilogSourceSet
import at.rknoll.gradle.hardware.vhdl.VhdlSourceSet
import java.io.File
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.process.ExecResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PshdlConverter {
	private Project project
    protected Logger logger

	public PshdlConverter(Project project) {
		this.project = project
        logger = LoggerFactory.getLogger('pshdl-logger')
	}

	public File convert(File file) {
		if (!file.name.endsWith(".pshdl")) return null

		def pshdlPath = PshdlUtils.findPshdlExecutable("pshdl.jar", project.pshdl as PshdlExtension)

		File destDir = project.file("generated/pshdl")
		destDir.mkdirs()

		File destFile = new File(destDir, file.name.substring(0, file.name.length() - 5) + "vhdl")

        def args = ["java", "-jar", pshdlPath, "vhdl", "-o", destDir.getAbsolutePath(), file.getAbsolutePath()]

        new ByteArrayOutputStream().withStream { os ->
            ExecResult result = project.exec {
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

		return destFile
	}

}
