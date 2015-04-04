package at.rknoll.gradle.hardware;

import org.gradle.api.Project;

import java.io.File;
import java.util.Set;

public class DefaultHardwareCompiler implements HardwareCompiler, Comparable<DefaultHardwareCompiler> {
	private final String name;
	private String description;
	private HardwareCompiler compiler;
	private int order;

	public DefaultHardwareCompiler(String name, Set<DefaultHardwareCompiler> compilers) {
		this.name = name;
		this.order = 0;

		if (compilers != null && !compilers.isEmpty()) {
			DefaultHardwareCompiler max = compilers.stream().max(DefaultHardwareCompiler::compareTo).get();
			if (max != null) {
				this.order = max.order + 1;
			}
		}
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

	public void setHardwareCompilerImpl(HardwareCompiler compiler) {
		this.compiler = compiler;
	}

	@Override
	public String toString() {
		return String.format("hardware compiler '%s'", getName());
	}

	public boolean compile(File file) {
		return compiler == null ? false : compiler.compile(file);
	}

	@Override
	public int compareTo(DefaultHardwareCompiler other) {
		return Integer.compare(this.order, other.order);
	}
}
