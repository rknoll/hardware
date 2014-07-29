package at.rknoll.gradle.hardware;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectSet;

public interface HardwareCompilerContainer extends NamedDomainObjectContainer<HardwareCompiler>, NamedDomainObjectSet<HardwareCompiler> {
}
