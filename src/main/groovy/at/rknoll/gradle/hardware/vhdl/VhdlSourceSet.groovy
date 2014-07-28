package at.rknoll.gradle.hardware.vhdl

import groovy.lang.Closure
import org.gradle.api.file.SourceDirectorySet

public interface VhdlSourceSet {

    SourceDirectorySet getVhdl()

    VhdlSourceSet vhdl(Closure configureClosure)

}
