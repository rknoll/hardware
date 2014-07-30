package at.rknoll.gradle.hardware.vhdl

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VhdlFindDependenciesTask extends SourceTask {

    @TaskAction
    def compile() {
        println "-- Vhdl Find Dependencies --"

        FileTree sources = getSource()

        sources.visit { file ->
			// find dependencies for this file
			/*project.sourceSets.main.getAllSource().each {
				if (!it.equals(file.file)) {
					project.hardwareSources.addEdge(it, file.file);
				}
			}*/
        }
    }

}
