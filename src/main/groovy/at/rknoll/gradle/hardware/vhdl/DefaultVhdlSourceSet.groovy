package at.rknoll.gradle.hardware.vhdl

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.ConfigureUtil

public class DefaultVhdlSourceSet implements VhdlSourceSet {
    private final SourceDirectorySet vhdl

    public DefaultVhdlSourceSet(String displayName, FileResolver fileResolver) {
        vhdl = new DefaultSourceDirectorySet(String.format("%s Vhdl source", displayName), fileResolver)
        vhdl.getFilter().include("**/*.vhdl", "**/*.vhd")
    }

    public SourceDirectorySet getVhdl() {
        return vhdl
    }

    public VhdlSourceSet vhdl(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getVhdl())
        return this
    }

}
