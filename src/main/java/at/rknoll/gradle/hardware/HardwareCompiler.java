package at.rknoll.gradle.hardware;

import java.io.File;
import java.util.Set;

/**
 * Defines a Hardware Compiler Wrapper that uses an Instance of a {@link HardwareCompilerImpl}.
 */
public class HardwareCompiler implements Comparable<HardwareCompiler> {
    /**
     * The name of this Compiler.
     */
    private final String name;

    /**
     * The description of this Compiler.
     */
    private String description;

    /**
     * The compiler implementation to be used.
     */
    private HardwareCompilerImpl compiler;

    /**
     * The specific order of this Compiler in the Compiler List.
     */
    private int order;

    /**
     * Creates a new Hardware Compiler with the given name and the current set of
     * defined Compilers.
     *
     * @param name      The Name of this new Hardware Compiler.
     * @param compilers The current set of Hardware Compilers.
     */
    public HardwareCompiler(final String name, final Set<HardwareCompiler> compilers) {
        this.name = name;
        this.order = 0;

        // check if there are any compilers already
        if (compilers != null && !compilers.isEmpty()) {
            // find the one with the highest order and order this behind it.
            // this will lead to the behaviour that the first defined Hardware Compiler
            // will be used if it is available.
            final HardwareCompiler max = compilers.stream().max(HardwareCompiler::compareTo).get();
            this.order = max.order + 1;
        }
    }

    /**
     * Get the name of this Hardware Compiler.
     *
     * @return The name of this Hardware Compiler.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of this Hardware Compiler.
     *
     * @return The description of this Hardware Compiler.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this Hardware Compiler.
     *
     * @param description The new description of this Hardware Compiler.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Set the Compiler Implementation to be used by this Compiler.
     *
     * @param compiler The new Compiler Implementation.
     */
    public void setHardwareCompilerImpl(final HardwareCompilerImpl compiler) {
        this.compiler = compiler;
    }

    /**
     * Compile Function.
     *
     * @param file The file to be compiled.
     * @return <code>true</code> if the compilation succeeded, <code>false</code> otherwise.
     */
    public boolean compile(final File file) {
        return compiler != null && compiler.compile(file);
    }

    @Override
    public String toString() {
        return String.format("hardware compiler '%s'", getName());
    }

    @Override
    public int compareTo(final HardwareCompiler other) {
        return Integer.compare(this.order, other.order);
    }
}
