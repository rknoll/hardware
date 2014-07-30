package at.rknoll.gradle.hardware.pshdl

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

class PshdlPrepareCompileTask extends SourceTask {
    @TaskAction
    def compile() {
		PshdlConverter converter = new PshdlConverter(project);

		File destDir = project.file("generated/pshdl")
		if (destDir.isDirectory()) destDir.deleteDir()
		destDir.mkdirs()

        FileTree sources = getSource()
		InputStream resourceStream = this.getClass().getResourceAsStream("/at/rknoll/gradle/hardware/pshdl/pshdl_pkg.vhd")
		File pshdlPkg = new File(destDir, "pshdl_pkg.vhd")
		Files.copy(resourceStream, pshdlPkg.getAbsoluteFile().toPath())
		
		project.hardwareSources.addVertex(pshdlPkg)

        sources.visit { file ->
			File destFile = converter.convert(file.file)
			if (destFile != null) {
				project.hardwareSources.addVertex(destFile)
				project.hardwareSources.addEdge(pshdlPkg, destFile);

				// so that other sources can reference the pshdl source as dependency..
				project.hardwareSources.addVertex(file.file)
				project.hardwareSources.addEdge(destFile, file.file);
			}
        }
    }

}
