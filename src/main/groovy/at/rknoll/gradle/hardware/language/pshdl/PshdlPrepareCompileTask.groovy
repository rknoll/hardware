package at.rknoll.gradle.hardware.language.pshdl

import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.gradle.hardware.HardwareSourceInformation
import at.rknoll.gradle.hardware.HardwareUtils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet

import java.nio.file.Files
import java.nio.file.StandardCopyOption

class PshdlPrepareCompileTask extends DefaultTask {
    protected final List<Object> source = new ArrayList<Object>();
    private final PatternFilterable patternSet = new PatternSet();
    public String sourceSet

    public FileTree getSource() {
        FileTree src = getProject().files(new ArrayList<Object>(this.source)).getAsFileTree();
        return src == null ? getProject().files().getAsFileTree() : src.matching(patternSet);
    }

    public void setSource(Object source) {
        this.source.clear();
        this.source.add(source);
    }

    public PshdlPrepareCompileTask source(Object... sources) {
        this.source.addAll(sources)
        return this;
    }

    @TaskAction
    def compile() {
        PshdlConverter converter = new PshdlConverter(project);

        File baseDir = project.file("generated/pshdl")

        def allPshdlFiles = []

        getSource().visit { file ->
            if (!file.isDirectory()) {
                allPshdlFiles.add(file.file)
            }
        }

        def sourceInfo = new HardwareSourceInformation()
        sourceInfo.group = project.group
        sourceInfo.name = project.name
        sourceInfo.version = project.version

        def convention = project.convention.getPlugin HardwarePluginConvention

        for (File file : convention.hardwareSources[sourceSet].vertexSet()) {
            if (!PshdlUtils.isPshdlFile(file)) continue
            def targetInfo = convention.hardwareSourceInformation[sourceSet][file]
            if (targetInfo.name.equals(sourceInfo.name) && targetInfo.group.equals(sourceInfo.group)) continue
            allPshdlFiles.add(file)
        }

        File pshdlPkg = null;

        for (File file : allPshdlFiles) {
            if (!PshdlUtils.isPshdlFile(file)) continue
            def fileInfo = convention.hardwareSourceInformation[sourceSet][file]
            if (fileInfo == null) fileInfo = sourceInfo

            def libraryName = HardwareUtils.getLibraryName(fileInfo.group, fileInfo.name);
            def destDir = new File(baseDir, libraryName)

            File destFile = converter.prepareConvert(file, destDir)
            if (destFile != null) {
                if (pshdlPkg == null) {
                    destDir.mkdirs()
                    InputStream resourceStream = this.getClass().getResourceAsStream("pshdl_pkg.vhd")
                    pshdlPkg = new File(destDir, "pshdl_pkg.vhd")
                    Files.copy(resourceStream, pshdlPkg.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING)
                    convention.hardwareSourceInformation[sourceSet][pshdlPkg] = fileInfo
                    convention.hardwareSources[sourceSet].addVertex(pshdlPkg)

                    if (SourceSet.MAIN_SOURCE_SET_NAME.equals(sourceSet)) {
                        project.sourceSets.all([execute: { SourceSet sourceSet ->
                            if (!convention.hardwareSources[sourceSet.name].containsVertex(pshdlPkg)) {
                                convention.hardwareSourceInformation[sourceSet.name][pshdlPkg] = fileInfo
                                convention.hardwareSources[sourceSet.name].addVertex(pshdlPkg)
                            }
                        }] as Action<SourceSet>)
                    }
                }
                convention.hardwareSourceInformation[sourceSet][destFile] = fileInfo
                convention.hardwareSources[sourceSet].addVertex(destFile)
                convention.hardwareSources[sourceSet].addEdge(pshdlPkg, destFile)

                if (SourceSet.MAIN_SOURCE_SET_NAME.equals(sourceSet)) {
                    project.sourceSets.all([execute: { SourceSet sourceSet ->
                        if (!convention.hardwareSources[sourceSet.name].containsVertex(destFile)) {
                            convention.hardwareSourceInformation[sourceSet.name][destFile] = fileInfo
                            convention.hardwareSources[sourceSet.name].addVertex(destFile)
                            convention.hardwareSources[sourceSet.name].addEdge(pshdlPkg, destFile)
                        }
                    }] as Action<SourceSet>)
                }

                // so that other sources can reference the pshdl source as dependency..
                convention.hardwareSourceInformation[sourceSet][file] = fileInfo
                convention.hardwareSources[sourceSet].addVertex(file)
                convention.hardwareSources[sourceSet].addEdge(destFile, file)

                if (SourceSet.MAIN_SOURCE_SET_NAME.equals(sourceSet)) {
                    project.sourceSets.all([execute: { SourceSet sourceSet ->
                        if (!convention.hardwareSources[sourceSet.name].containsVertex(file)) {
                            convention.hardwareSourceInformation[sourceSet.name][file] = fileInfo
                            convention.hardwareSources[sourceSet.name].addVertex(file)
                            convention.hardwareSources[sourceSet.name].addEdge(destFile, file)
                        }
                    }] as Action<SourceSet>)
                }
            }
        }

        converter.convert()
    }

}
