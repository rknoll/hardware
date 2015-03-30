package at.rknoll.gradle.hardware.questasim

class QuestasimUtils {

    private static final Set<String> QUESTASIM_DIRS = new HashSet<String>(Arrays.asList(
        [ "questasim", "win32", "questa" ] as String []
    ));

    private static boolean matches(String filename, String executable) {
        if (executable.lastIndexOf('.') <= 0) {
            int pos = filename.lastIndexOf('.');
			if (pos > 0 && filename.substring(pos) != ".exe") return false
            filename = pos > 0 ? filename.substring(0, pos) : filename;
        }
        return executable.equals(filename)
    }

    public static String findQuestasimExecutable(String executable, QuestasimExtension options) {
        ArrayList<String> subDirectories = new ArrayList<String>(options.paths)
        ArrayList<String> foundInstances = new ArrayList<String>()

        if (options.path) subDirectories = Arrays.asList([ options.path ] as String [])

        int baseCount = subDirectories.size()

        for(int i = 0; i < subDirectories.size(); i++) {
            File baseFile = new File(subDirectories.get(i))
            if (!baseFile.isDirectory() && matches(baseFile.getName(), executable)) foundInstances.add(baseFile.getAbsolutePath())

            for (File path : baseFile.listFiles()) {
                if (!path.isDirectory() && matches(baseFile.getName(), executable)) foundInstances.add(path.getAbsolutePath())
                if (QUESTASIM_DIRS.contains(baseFile.getName()) || QUESTASIM_DIRS.contains(path.getName()) || i < baseCount) {
                    if (!subDirectories.contains(path.getAbsolutePath())) subDirectories.add(path.getAbsolutePath())
                }
            }
        }

        if (foundInstances.empty) {
            //throw new RuntimeException("Could not locate Questasim Executable. Please define your local Installation Path.")
            return null
        } else if (foundInstances.size() > 1) {
            //for (String instance : foundInstances) {
            //    System.err.println(instance)
            //}
            //throw new RuntimeException("Multiple Questasim Installations found. Please define your local Installation Path.")
            return null
        }

        return foundInstances.get(0)
    }

}
