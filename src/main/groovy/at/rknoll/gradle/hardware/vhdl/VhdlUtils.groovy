package at.rknoll.gradle.hardware.vhdl

class VhdlUtils {

    public static boolean isVhdlFile(File file) {
		String fileName = file.getAbsolutePath()
		return VhdlSourceSet.EXTENSIONS.find {
			return fileName.endsWith(it)
		} != null
    }

}
