package at.rknoll.gradle.hardware.pshdl

import org.gradle.api.file.FileTree
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

class PshdlPrepareCompileTask extends DefaultTask {
    @TaskAction
    def compile() {
		PshdlConverter converter = new PshdlConverter(project);

		File destDir = project.file("generated/pshdl")
		if (destDir.isDirectory()) destDir.deleteDir()
		destDir.mkdirs()

		InputStream resourceStream = this.getClass().getResourceAsStream("/at/rknoll/gradle/hardware/pshdl/pshdl_pkg.vhd")
		File pshdlPkg = new File(destDir, "pshdl_pkg.vhd")
		Files.copy(resourceStream, pshdlPkg.getAbsoluteFile().toPath())
		
		project.hardwareSources.addVertex(pshdlPkg)

		def allPshdlFiles = []

        FileTree sources = project.sourceSets.main.pshdl.visit { file ->
			allPshdlFiles.add(file.file)
		}

		for (File file : project.hardwareSources.vertexSet()) {
			if (!PshdlUtils.isPshdlFile(file)) continue
			allPshdlFiles.add(file)
		}

		for (File file : allPshdlFiles) {
			if (!PshdlUtils.isPshdlFile(file)) continue
			File destFile = converter.prepareConvert(file)
			if (destFile != null) {
				project.hardwareSources.addVertex(destFile)
				project.hardwareSources.addEdge(pshdlPkg, destFile);

				// so that other sources can reference the pshdl source as dependency..
				project.hardwareSources.addVertex(file)
				project.hardwareSources.addEdge(destFile, file);
			}
        }

		converter.convert()
    }

}
