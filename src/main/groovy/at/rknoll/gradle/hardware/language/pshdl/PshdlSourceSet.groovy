package at.rknoll.gradle.hardware.language.pshdl

import org.gradle.api.file.SourceDirectorySet

public interface PshdlSourceSet {
    public static final String[] EXTENSIONS = [".pshdl"];

    SourceDirectorySet getPshdl()

    PshdlSourceSet pshdl(Closure configureClosure)

}
