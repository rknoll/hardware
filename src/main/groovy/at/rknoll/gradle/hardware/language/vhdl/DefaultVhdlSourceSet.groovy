package at.rknoll.gradle.hardware.language.vhdl

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.ConfigureUtil

public class DefaultVhdlSourceSet implements VhdlSourceSet {
    private final SourceDirectorySet vhdl

    public DefaultVhdlSourceSet(String displayName, FileResolver fileResolver) {
        vhdl = new DefaultSourceDirectorySet(String.format("%s Vhdl source", displayName), fileResolver)
        EXTENSIONS.each {
            vhdl.getFilter().include("**/*" + it)
        }
    }

    public SourceDirectorySet getVhdl() {
        return vhdl
    }

    public VhdlSourceSet vhdl(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getVhdl())
        return this
    }

}
