package at.rknoll.gradle.hardware.pshdl

import at.rknoll.gradle.hardware.HardwareCompiler
import org.gradle.api.Project

class PshdlDummyCompilerImpl implements HardwareCompiler {
	public boolean compile(File file) {
		return (PshdlSourceSet.EXTENSIONS.find { file.name.endsWith(it) } != null);
	}
}
