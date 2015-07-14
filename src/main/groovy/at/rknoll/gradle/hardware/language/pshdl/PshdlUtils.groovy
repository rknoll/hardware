package at.rknoll.gradle.hardware.language.pshdl

class PshdlUtils {

    private static final Set<String> PSHDL_DIRS = new HashSet<String>(Arrays.asList(
        [ "pshdl" ] as String []
    ));

    public static boolean isPshdlFile(File file) {
		String fileName = file.getAbsolutePath()
		return PshdlSourceSet.EXTENSIONS.find {
			return fileName.endsWith(it)
		} != null
    }

    private static boolean matches(String filename, String executable) {
        if (executable.lastIndexOf('.') <= 0) {
            int pos = filename.lastIndexOf('.');
			if (pos > 0 && filename.substring(pos) != ".jar") return false
            filename = pos > 0 ? filename.substring(0, pos) : filename;
        }
        return executable.equals(filename)
    }

    public static String findPshdlExecutable(String executable, PshdlExtension options) {
        ArrayList<String> subDirectories = new ArrayList<String>(options.paths)
        ArrayList<String> foundInstances = new ArrayList<String>()

        if (options.path) subDirectories = Arrays.asList([ options.path ] as String [])

        int baseCount = subDirectories.size()

        for(int i = 0; i < subDirectories.size(); i++) {
            File baseFile = new File(subDirectories.get(i))
            if (!baseFile.isDirectory() && matches(baseFile.getName(), executable)) foundInstances.add(baseFile.getAbsolutePath())

            for (File path : baseFile.listFiles()) {
                if (!path.isDirectory() && matches(baseFile.getName(), executable)) foundInstances.add(path.getAbsolutePath())
                if (PSHDL_DIRS.contains(baseFile.getName()) || PSHDL_DIRS.contains(path.getName()) || i < baseCount) {
                    if (!subDirectories.contains(path.getAbsolutePath())) subDirectories.add(path.getAbsolutePath())
                }
            }
        }

        if (foundInstances.empty) {
            return null;
        } else if (foundInstances.size() > 1) {
            for (String instance : foundInstances) {
                System.err.println(instance)
            }
            throw new RuntimeException("Multiple Pshdl Installations found. Please define your local Installation Path.")
        }

        return foundInstances.get(0)
    }

}
