package at.rknoll.gradle.hardware.language.pshdl

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.ConfigureUtil

public class DefaultPshdlSourceSet implements PshdlSourceSet {
    private final SourceDirectorySet pshdl

    public DefaultPshdlSourceSet(String displayName, FileResolver fileResolver) {
        pshdl = new DefaultSourceDirectorySet(String.format("%s Pshdl source", displayName), fileResolver)
        EXTENSIONS.each {
            pshdl.getFilter().include("**/*" + it)
        }
    }

    public SourceDirectorySet getPshdl() {
        return pshdl
    }

    public PshdlSourceSet pshdl(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getPshdl())
        return this
    }

}
