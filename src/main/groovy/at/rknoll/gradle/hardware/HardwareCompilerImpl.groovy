package at.rknoll.gradle.hardware
/**
 * The Base Interface of a Hardware Compiler Implementation.
 */
public interface HardwareCompilerImpl {
    /**
     * Compile Function.
     *
     * @param file The file to be compiled.
     * @return <code>true</code> if the compilation succeeded, <code>false</code> otherwise.
     */
    boolean compile(File file);
}
