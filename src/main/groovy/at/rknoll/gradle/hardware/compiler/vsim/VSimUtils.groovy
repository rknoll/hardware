package at.rknoll.gradle.hardware.compiler.vsim

class VSimUtils {

    private static boolean matches(String filename, String executable) {
        if (executable.lastIndexOf('.') <= 0) {
            int pos = filename.lastIndexOf('.');
            if (pos > 0 && filename.substring(pos) != ".exe") return false
            filename = pos > 0 ? filename.substring(0, pos) : filename;
        }
        return executable.equals(filename)
    }

    public static String findVSimExecutable(String executable, VSimExtension options, Set<String> paths) {
        ArrayList<String> subDirectories = new ArrayList<String>(options.paths)
        ArrayList<String> foundInstances = new ArrayList<String>()

        if (options.path) subDirectories = Arrays.asList([options.path] as String[])

        int baseCount = subDirectories.size()

        for (int i = 0; i < subDirectories.size(); i++) {
            File baseFile = new File(subDirectories.get(i))
            println "baseFile: " + baseFile
            if (!baseFile.isDirectory() && matches(baseFile.getName(), executable)) foundInstances.add(baseFile.getAbsolutePath())

            for (File path : baseFile.listFiles()) {
                if (!path.isDirectory() && matches(baseFile.getName(), executable)) foundInstances.add(path.getAbsolutePath())
                if (paths.contains(baseFile.getName()) || paths.contains(path.getName()) || i < baseCount) {
                    if (!subDirectories.contains(path.getAbsolutePath())) subDirectories.add(path.getAbsolutePath())
                }
            }
        }
        println "foundInstances: " + foundInstances
        println "foundInstances: " + foundInstances.size()

        return (foundInstances.empty || foundInstances.size() > 1) ? null : foundInstances.get(0)
    }

}
