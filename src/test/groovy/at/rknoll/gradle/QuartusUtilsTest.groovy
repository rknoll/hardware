package at.rknoll.gradle

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*
import java.io.*
import java.nio.file.*

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
