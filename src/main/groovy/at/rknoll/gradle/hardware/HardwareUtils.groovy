package at.rknoll.gradle.hardware

/**
 * Created by Richard on 30.03.2015.
 */
class HardwareUtils {

    public static String getLibraryName(String group, String name) {
        return group.replace('.', '_').toLowerCase() + "_" + name.toLowerCase()
    }

}
