package at.rknoll.gradle.hardware.language.verilog

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.ConfigureUtil

public class DefaultVerilogSourceSet implements VerilogSourceSet {
    private final SourceDirectorySet verilog

    public DefaultVerilogSourceSet(String displayName, FileResolver fileResolver) {
        verilog = new DefaultSourceDirectorySet(String.format("%s Verilog source", displayName), fileResolver)
		VerilogSourceSet.EXTENSIONS.each {
			verilog.getFilter().include("**/*" + it)
		}
    }

    public SourceDirectorySet getVerilog() {
        return verilog
    }

    public VerilogSourceSet verilog(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getVerilog())
        return this
    }

}
