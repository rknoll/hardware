package at.rknoll.gradle.hardware

/**
 * Util Class for all Hardware Plugins.
 */
class HardwareUtils {

    /**
     * Gets the Library Name for a given group and entity name.
     * @param group The Group.
     * @param name The Entity Name.
     * @return The Library Name.
     */
    public static String getLibraryName(String group, String name) {
        return group.replace('.', '_').toLowerCase() + "_" + name.toLowerCase()
    }

}
