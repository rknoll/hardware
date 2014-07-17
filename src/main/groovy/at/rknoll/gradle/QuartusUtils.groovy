package at.rknoll.gradle

import java.lang.*
import java.util.*

class QuartusUtils {

    public static final String QUARTUS_EXE = "quartus.exe"
    
    private static final Set<String> QUARTUS_DIRS = new HashSet<String>(Arrays.asList(
        [ "altera", "quartus", "bin64", "bin" ] as String []
    ));
    
    public static String findQuartusExecutable(String[] basePaths) {
        ArrayList<String> subDirectories = Arrays.asList(basePaths)
        
        for(int i = 0; i < subDirectories.size(); i++) {
            File baseFile = new File(subDirectories.get(i))
            if (!baseFile.isDirectory() && QUARTUS_EXE.equals(baseFile.getName())) return baseFile.getAbsolutePath()

            for (File path : baseFile.listFiles()) {
                if (!path.isDirectory() && QUARTUS_EXE.equals(path.getName())) return path.getAbsolutePath()
                if (QUARTUS_DIRS.contains(baseFile.getName()) || QUARTUS_DIRS.contains(path.getName()) || i < basePaths.length) {
                    if (!subDirectories.contains(path.getAbsolutePath())) subDirectories.add(path.getAbsolutePath())
                }
            }
        }
        
        throw new Exception("Could not locate Quartus Executable. Please define your local Installation Path." + subDirectories.get(0))
    }
    
}
