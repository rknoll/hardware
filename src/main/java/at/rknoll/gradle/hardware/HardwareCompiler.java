package at.rknoll.gradle.hardware;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectSet;

public interface HardwareCompiler {
	String getName();
	String getDescription();
}
