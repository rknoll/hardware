package at.rknoll.gradle.hardware;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectSet;

public class DefaultHardwareCompiler implements HardwareCompiler {
	private final String name;
	private String description;

	public DefaultHardwareCompiler(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return String.format("hardware compiler '%s'", getName());
	}
}
