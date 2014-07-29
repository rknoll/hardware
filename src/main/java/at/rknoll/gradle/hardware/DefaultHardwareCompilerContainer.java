package at.rknoll.gradle.hardware;

import org.gradle.api.Namer;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.internal.reflect.Instantiator;

public class DefaultHardwareCompilerContainer extends AbstractNamedDomainObjectContainer<HardwareCompiler> implements HardwareCompilerContainer {
	public DefaultHardwareCompilerContainer(Instantiator classGenerator) {
		super(HardwareCompiler.class, classGenerator, new Namer<HardwareCompiler>() { public String determineName(HardwareCompiler compiler) { return compiler.getName(); }});
	}
	
	@Override
    protected HardwareCompiler doCreate(String name) {
        //DefaultSourceSet sourceSet = instantiator.newInstance(DefaultSourceSet.class, name, fileResolver);
        //sourceSet.setClasses(instantiator.newInstance(DefaultSourceSetOutput.class, sourceSet.getDisplayName(), fileResolver, taskResolver));

        return null;//sourceSet;
    }
}
