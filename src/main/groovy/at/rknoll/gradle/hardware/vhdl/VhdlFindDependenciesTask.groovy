package at.rknoll.gradle.hardware.vhdl

import org.gradle.api.file.FileTree
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import java.util.regex.Matcher

class VhdlFindDependenciesTask extends DefaultTask {

    @TaskAction
    def compile() {
		def dependsOn = [:]
		def definesUnits = [:]

		for (File file : project.hardwareSources.vertexSet()) {
			if (!VhdlUtils.isVhdlFile(file)) continue
			String fileContents = file.text
			dependsOn[file] = []
			definesUnits[file] = []
			fileContents.replace('\r','').tokenize('\n').each {

				Matcher matcher = (it =~ /^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))work\.([^\.\s\t\(]+).*/);
				if (matcher.matches()) {
					String matching = matcher[0][1].toLowerCase();
					if (!definesUnits[file].contains(matching) && !dependsOn[file].contains(matching)) {
						dependsOn[file].add(matching);
					}
				}

				matcher = (it =~ /(?:(?:^(?:(?:entity)|(?:package)))|(?:^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))(?:(?:entity)|(?:package))))[\s\t]+(?:(?:([^\s\t]+)[\s\t]+is)|(?:body[\s\t]+([^\s\t]+)[\s\t]+is)).*/);
				if (matcher.matches()) {
					String matching = matcher[0][1] == null ? null : matcher[0][1].toLowerCase();
					if (matching != null && !definesUnits[file].contains(matching)) {
						definesUnits[file].add(matching);
					}
					matching = matcher[0][2] == null ? null : matcher[0][2].toLowerCase();
					if (matching != null && !definesUnits[file].contains(matching) && !dependsOn[file].contains(matching)) {
						dependsOn[file].add(matching);
					}
				}

				matcher = (it =~ /(?:(?:^(?:architecture))|(?:^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))(?:architecture)))[\s\t]+(?:[^\s\t]+)[\s\t]+of[\s\t]+([^\s\t]+)[\s\t]+is.*/);
				if (matcher.matches()) {
					String matching = matcher[0][1].toLowerCase();
					if (!definesUnits[file].contains(matching) && !dependsOn[file].contains(matching)) {
						dependsOn[file].add(matching);
					}
				}
			}
		}

		for (File file : project.hardwareSources.vertexSet()) {
			if (!VhdlUtils.isVhdlFile(file)) continue
			println file.name + ": " + definesUnits[file] + "->" + dependsOn[file] 
			dependsOn[file].each { depId ->
				definesUnits.each { defFile, unitList ->
					if (unitList.contains(depId)) {
						if (!project.hardwareSources.containsEdge(defFile, file)) project.hardwareSources.addEdge(defFile, file);
					}
				}
			}
		}
    }

}
