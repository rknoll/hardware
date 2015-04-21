package at.rknoll.gradle.hardware

import org.gradle.api.file.FileTree
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class HardwarePrepareCompileTask extends DefaultTask {

    @TaskAction
    def compile() {
		def libsDirName = "libs/"

		for (def art : project.configurations.compile.resolvedConfiguration.resolvedArtifacts) {
			def moduleDirName = libsDirName + HardwareUtils.getLibraryName(art.moduleVersion.id.group, art.moduleVersion.id.name)
			def moduleDir = new File(moduleDirName)
			project.copy {
				from {
					project.zipTree(art.file).matching {
						exclude { it.path.contains('compile') }
					}
				}
				into moduleDir
			}
			def source = new HardwareSourceInformation()
			source.group = art.moduleVersion.id.group
			source.name = art.moduleVersion.id.name
			source.version = art.moduleVersion.id.version
			FileTree sources = project.fileTree(dir: moduleDirName)
			sources.visit { file ->
				project.hardwareSourceInformation[file.file] = source
				project.hardwareSources.addVertex(file.file)
			}
		}

    }

}
