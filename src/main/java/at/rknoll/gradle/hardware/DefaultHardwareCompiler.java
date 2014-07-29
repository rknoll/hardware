package at.rknoll.gradle.hardware;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectSet;
import java.io.File;

public class DefaultHardwareCompiler implements HardwareCompiler {
	private final String name;
	private String description;
	private HardwareCompilerImpl compiler;

	public DefaultHardwareCompiler(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setHardwareCompilerImpl(HardwareCompilerImpl compiler) {
		this.compiler = compiler;
	}

	@Override
	public String toString() {
		return String.format("hardware compiler '%s'", getName());
	}

	public boolean compile(File file) {
		return compiler == null ? false : compiler.compile(file);
	}
}
