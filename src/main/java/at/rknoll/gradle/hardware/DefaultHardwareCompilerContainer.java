package at.rknoll.gradle.hardware;

import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.internal.reflect.Instantiator;

/**
 * Default Implementation of a {@link HardwareCompiler}.
 */
public class DefaultHardwareCompilerContainer extends AbstractNamedDomainObjectContainer<HardwareCompiler> implements HardwareCompilerContainer {
    private final Instantiator instantiator;

    /**
     * Creates a new Hardware Compiler Container that holds all Compilers in the Project.
     *
     * @param classGenerator The generator to be used to create new Compilers.
     */
    public DefaultHardwareCompilerContainer(final Instantiator classGenerator) {
        super(HardwareCompiler.class, classGenerator, HardwareCompiler::getName);
        this.instantiator = classGenerator;
    }

    @Override
    protected HardwareCompiler doCreate(final String name) {
        // calls the Constructor of HardwareCompiler and passes the given name and
        // this (the Container of Compilers) to it.
        return instantiator.newInstance(HardwareCompiler.class, name, this);
    }
}
