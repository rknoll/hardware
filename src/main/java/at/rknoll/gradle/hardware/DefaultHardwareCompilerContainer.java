package at.rknoll.gradle.hardware;

import org.gradle.api.Namer;
import org.gradle.api.Project;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.internal.reflect.Instantiator;

public class DefaultHardwareCompilerContainer extends AbstractNamedDomainObjectContainer<DefaultHardwareCompiler> implements HardwareCompilerContainer {
	private final Instantiator instantiator;
	private final Project project;

	public DefaultHardwareCompilerContainer(Instantiator classGenerator, Project project) {
		super(DefaultHardwareCompiler.class, classGenerator, HardwareCompiler::getName);
		this.instantiator = classGenerator;
		this.project = project;
	}

	@Override
	protected DefaultHardwareCompiler doCreate(String name) {
		return instantiator.newInstance(DefaultHardwareCompiler.class, name, project);
	}
}
