package at.rknoll.gradle.hardware;

import org.gradle.api.Namer;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.internal.reflect.Instantiator;

public class DefaultHardwareCompilerContainer extends AbstractNamedDomainObjectContainer<HardwareCompiler> implements HardwareCompilerContainer {
	private final Instantiator instantiator;

	public DefaultHardwareCompilerContainer(Instantiator classGenerator) {
		super(HardwareCompiler.class, classGenerator, HardwareCompiler::getName);
		this.instantiator = classGenerator;
	}

	@Override
	protected HardwareCompiler doCreate(String name) {
		return instantiator.newInstance(DefaultHardwareCompiler.class, name);
	}
}
