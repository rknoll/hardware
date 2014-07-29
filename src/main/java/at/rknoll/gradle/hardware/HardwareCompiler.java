package at.rknoll.gradle.hardware;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectSet;
import java.io.File;

public interface HardwareCompiler {
	String getName();
	String getDescription();
	boolean compile(File file);
}
