package at.rknoll.gradle.hardware.quartus

import org.junit.Test

import java.nio.file.Files

class QuartusUtilsTest {

    @Test
    public void findsQuartusInTopDir() {
        File dir = Files.createTempDirectory(null).toFile()
        File file = new File(dir, "quartus.exe")

        file.createNewFile()
        QuartusExtension ext = new QuartusExtension();
        ext.path = dir.getAbsolutePath()

        QuartusUtils.findQuartusExecutable("quartus.exe", ext)

        dir.deleteOnExit()
        file.deleteOnExit()
    }

}
