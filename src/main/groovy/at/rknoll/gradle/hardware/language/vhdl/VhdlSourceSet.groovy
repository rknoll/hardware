package at.rknoll.gradle.hardware.language.vhdl

import org.gradle.api.file.SourceDirectorySet

public interface VhdlSourceSet {
	public static final String[] EXTENSIONS = [ ".vhd", ".vhdl" ];

    SourceDirectorySet getVhdl()

    VhdlSourceSet vhdl(Closure configureClosure)

}
