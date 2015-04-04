package at.rknoll.gradle.hardware;

import org.gradle.api.Project;

import java.io.File;

public class DefaultHardwareCompiler implements HardwareCompiler, Comparable<DefaultHardwareCompiler> {
	private final String name;
	private String description;
	private HardwareCompiler compiler;
	protected Project project;
	private int order;

	public DefaultHardwareCompiler(String name, Project project) {
		this.project = project;
		this.name = name;


		HardwarePluginConvention hardwareConvention = project.getConvention().getPlugin(HardwarePluginConvention.class);
		HardwareCompilerContainer compilers = hardwareConvention.getHardwareCompilers();
/*
		compilers.stream().max((a, b) -> a. a.com)

		if (compilers.find { ModelsimAlteraCompilerImpl.NAME.equals(it.name) } == null) {
			compilers.create(ModelsimAlteraCompilerImpl.NAME, {
					it.setDescription("compile with modelsimaltera")
					it.setHardwareCompilerImpl(new ModelsimAlteraCompilerImpl(project))
			});
		}
		*/
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
