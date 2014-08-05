package at.rknoll.gradle.hardware.vhdl

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import java.util.regex.Matcher

class VhdlFindDependenciesTask extends SourceTask {

    @TaskAction
    def compile() {
        FileTree sources = getSource()

		// depend on all external dependencies by default
		for (File file : project.hardwareSources.vertexSet()) {
			if (!sources.contains(file)) {
				sources.visit { src ->
					project.hardwareSources.addEdge(file, src.file);
				}
			}
		}

		def dependsOn = [:]
		def definesUnits = [:]

		for (File file : project.hardwareSources.vertexSet()) {
			String fileContents = file.text
			dependsOn[file] = []
			definesUnits[file] = []
			fileContents.replace('\r','').tokenize('\n').each {
				Matcher matcher = (it =~ /^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))work\.([^\.\s\t\(]+).*/);
				if (matcher.matches()) dependsOn[file].add(matcher[0][1].toLowerCase())
				matcher = (it =~ /(?:(?:^(?:(?:entity)|(?:package)))|(?:^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))(?:(?:entity)|(?:package))))[\s\t]+([^\s\t]+)[\s\t]+is.*/);
				if (matcher.matches()) definesUnits[file].add(matcher[0][1].toLowerCase())
			}
		}

		for (File file : project.hardwareSources.vertexSet()) {
			dependsOn[file].each { depId ->
				definesUnits.each { defFile, unitList ->
					if (unitList.contains(depId)) {
						project.hardwareSources.addEdge(defFile, file);
					}
				}
			}
		}
    }

}
