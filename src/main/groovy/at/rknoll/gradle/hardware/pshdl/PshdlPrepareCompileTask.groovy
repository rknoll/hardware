package at.rknoll.gradle.hardware.pshdl

import at.rknoll.gradle.hardware.HardwareSourceInformation
import at.rknoll.gradle.hardware.HardwareUtils
import org.gradle.api.file.FileTree
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet

import java.nio.file.Files
import java.nio.file.StandardCopyOption

class PshdlPrepareCompileTask extends DefaultTask {
	protected final List<Object> source = new ArrayList<Object>();
	private final PatternFilterable patternSet = new PatternSet();

	public FileTree getSource() {
		FileTree src = getProject().files(new ArrayList<Object>(this.source)).getAsFileTree();
		return src == null ? getProject().files().getAsFileTree() : src.matching(patternSet);
	}

	public void setSource(Object source) {
		this.source.clear();
		this.source.add(source);
	}

	public PshdlPrepareCompileTask source(Object... sources) {
		for (Object source : sources) {
			this.source.add(source);
		}
		return this;
	}

    @TaskAction
    def compile() {
		PshdlConverter converter = new PshdlConverter(project);

		File baseDir = project.file("generated/pshdl")

		def allPshdlFiles = []

		getSource().visit { file ->
			allPshdlFiles.add(file.file)
		}

		def sourceInfo = new HardwareSourceInformation()
		sourceInfo.group = project.group
		sourceInfo.name = project.name
		sourceInfo.version = project.version

		for (File file : project.hardwareSources.vertexSet()) {
			if (!PshdlUtils.isPshdlFile(file)) continue
			def targetInfo = project.hardwareSourceInformation[file]
			if (targetInfo.name.equals(sourceInfo.name) && targetInfo.group.equals(sourceInfo.group)) continue
			allPshdlFiles.add(file)
		}

		File pshdlPkg = null;

		for (File file : allPshdlFiles) {
			if (!PshdlUtils.isPshdlFile(file)) continue
			def fileInfo = project.hardwareSourceInformation[file]
			if (fileInfo == null) fileInfo = sourceInfo


			def libraryName = HardwareUtils.getLibraryName(fileInfo.group, fileInfo.name);
			def destDir = new File(baseDir, libraryName)

			File destFile = converter.prepareConvert(file, destDir)
			if (destFile != null) {
				if (pshdlPkg == null) {
					destDir.mkdirs()
					InputStream resourceStream = this.getClass().getResourceAsStream("/at/rknoll/gradle/hardware/pshdl/pshdl_pkg.vhd")
					pshdlPkg = new File(destDir, "pshdl_pkg.vhd")
					Files.copy(resourceStream, pshdlPkg.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING)
					project.hardwareSourceInformation[pshdlPkg] = fileInfo
					project.hardwareSources.addVertex(pshdlPkg)
				}
				project.hardwareSourceInformation[destFile] = fileInfo
				project.hardwareSources.addVertex(destFile)
				project.hardwareSources.addEdge(pshdlPkg, destFile);

				// so that other sources can reference the pshdl source as dependency..
				project.hardwareSourceInformation[file] = fileInfo
				project.hardwareSources.addVertex(file)
				project.hardwareSources.addEdge(destFile, file);
			}
        }

		converter.convert()
    }

}
