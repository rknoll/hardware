package at.rknoll.gradle.hardware.language.verilog

import org.gradle.api.file.SourceDirectorySet

public interface VerilogSourceSet {
    public static final String[] EXTENSIONS = [".v", ".verilog"];

    SourceDirectorySet getVerilog()

    VerilogSourceSet verilog(Closure configureClosure)

}
