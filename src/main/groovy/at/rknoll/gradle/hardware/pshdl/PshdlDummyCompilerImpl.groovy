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

class PshdlDummyCompilerImpl implements HardwareCompilerImpl {
	public boolean compile(File file) {
		return (PshdlSourceSet.EXTENSIONS.find { file.name.endsWith(it) } != null);
	}
}
