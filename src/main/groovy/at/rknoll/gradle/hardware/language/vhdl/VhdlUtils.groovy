package at.rknoll.gradle.hardware.language.vhdl

class VhdlUtils {

    public static boolean isVhdlFile(File file) {
		final String fileName = file.getAbsolutePath()
		return VhdlSourceSet.EXTENSIONS.find { fileName.endsWith(it) } != null
    }

}
