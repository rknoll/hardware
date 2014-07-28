package at.rknoll.gradle.hardware.verilog

import org.gradle.api.file.SourceDirectorySet

public interface VerilogSourceSet {

    SourceDirectorySet getVerilog()

    VerilogSourceSet verilog(Closure configureClosure)

}
