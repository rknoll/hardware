package at.rknoll.gradle.hardware.language.pshdl

import at.rknoll.gradle.hardware.HardwareCompilerImpl

class PshdlDummyCompilerImpl implements HardwareCompilerImpl {
    public boolean compile(File file) {
        return (PshdlSourceSet.EXTENSIONS.find { file.name.endsWith(it) } != null);
    }
}
