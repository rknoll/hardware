package at.rknoll.gradle.hardware.modelsimaltera

class ModelsimAlteraUtils {

    private static final Set<String> MODELSIM_ALTERA_DIRS = new HashSet<String>(Arrays.asList(
        [ "altera", "modelsim_ase", "win32aloem" ] as String []
    ));

    private static boolean matches(String filename, String executable) {
        if (executable.lastIndexOf('.') <= 0) {
            int pos = filename.lastIndexOf('.');
            filename = pos > 0 ? filename.substring(0, pos) : filename;
        }
        return executable.equals(filename)
    }

    public static String findModelsimAlteraExecutable(String executable, ModelsimAlteraExtension options) {
        ArrayList<String> subDirectories = new ArrayList<String>(options.paths)
        ArrayList<String> foundInstances = new ArrayList<String>()

        if (options.path) subDirectories = Arrays.asList([ options.path ] as String [])

        int baseCount = subDirectories.size()

        for(int i = 0; i < subDirectories.size(); i++) {
            File baseFile = new File(subDirectories.get(i))
            if (!baseFile.isDirectory() && matches(baseFile.getName(), executable)) foundInstances.add(baseFile.getAbsolutePath())

            for (File path : baseFile.listFiles()) {
                if (!path.isDirectory() && matches(baseFile.getName(), executable)) foundInstances.add(path.getAbsolutePath())
                if (MODELSIM_ALTERA_DIRS.contains(baseFile.getName()) || MODELSIM_ALTERA_DIRS.contains(path.getName()) || i < baseCount) {
                    if (!subDirectories.contains(path.getAbsolutePath())) subDirectories.add(path.getAbsolutePath())
                }
            }
        }

        if (!options.use32bit) {
            if (foundInstances.find { it.contains(File.separator + "bin64" + File.separator) }) {
                foundInstances.removeAll { it.contains(File.separator + "bin" + File.separator) }
            }
        } else {
            foundInstances.removeAll { it.contains(File.separator + "bin64" + File.separator) }
        }

        if (foundInstances.empty) {
		println "3"
            throw new RuntimeException("Could not locate ModelsimAltera Executable. Please define your local Installation Path.")
        } else if (foundInstances.size() > 1) {
            for (String instance : foundInstances) {
                System.err.println(instance)
            }
            throw new RuntimeException("Multiple ModelsimAltera Installations found. Please define your local Installation Path.")
        }

        return foundInstances.get(0)
    }

}
