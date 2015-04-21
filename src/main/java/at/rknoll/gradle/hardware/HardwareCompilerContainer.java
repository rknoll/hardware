package at.rknoll.gradle.hardware;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectSet;

/**
 * The Interface of a Hardware Compiler Container to be used internally.
 */
public interface HardwareCompilerContainer extends NamedDomainObjectContainer<HardwareCompiler>, NamedDomainObjectSet<HardwareCompiler> {
}
