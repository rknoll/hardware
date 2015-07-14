package at.rknoll.gradle.hardware.language.vhdl

import at.rknoll.parser.vhdl.VhdlLexer
import at.rknoll.parser.vhdl.VhdlParser
import org.antlr.runtime.ANTLRFileStream
import org.antlr.runtime.ANTLRInputStream
import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.CharStream
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.TokenStream
import org.antlr.runtime.tree.CommonTree
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.regex.Matcher
import java.util.regex.Pattern

class VhdlFindDependenciesTask extends DefaultTask {


    private void printTree(CommonTree ast) {
        print(ast, 0);
    }

    private void print(CommonTree tree, int level) {
        //indent level
        for (int i = 0; i < level; i++)
            System.out.print("--");

        //print node description: type code followed by token text
        System.out.println(" " + tree.getType() + " " + tree.getText());

        //print all children
        if (tree.getChildren() != null)
            for (Object ie : tree.getChildren()) {
                print((CommonTree) ie, level + 1);
            }
    }

    @TaskAction
    def compile() {
		def dependsOn = [:]
		def definesUnits = [:]

		for (File file : project.hardwareSources.vertexSet()) {
			if (!VhdlUtils.isVhdlFile(file)) continue

            // construct lexer and parsers
            def stream = new ANTLRStringStream(file.text)
            def lexer = new VhdlLexer(stream)
            def tokenStream = new CommonTokenStream(lexer)
            def parser = new VhdlParser(tokenStream)

            // parse design file
            def designFile = parser.design_file()

            // get parse tree
            def tree = (CommonTree)designFile.tree
            printTree(tree)

			String fileContents = file.text
			dependsOn[file] = []
			definesUnits[file] = []
			println "analyzing " + file.name + "..."
			Pattern p1 = Pattern.compile(/(?:(?:^(?:use))|(?:^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))(?:use)))[\s\t]+([^\.\s\t]+)\.([^\.\s\t\(]+)\..*/);
			Pattern p2 = Pattern.compile(/(?:(?:^(?:entity))|(?:^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))(?:.*entity)))[\s\t]+([^\.\s\t]+)\.([^\.\s\t\(]+).*/);
			Pattern p3 = Pattern.compile(/(?:(?:^(?:(?:entity)|(?:package)))|(?:^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))(?:(?:entity)|(?:package))))[\s\t]+(?:(?:([^\s\t]+)[\s\t]+is)|(?:body[\s\t]+([^\s\t]+)[\s\t]+is)).*/);
			Pattern p4 = Pattern.compile(/(?:(?:^(?:architecture))|(?:^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))(?:architecture)))[\s\t]+(?:[^\s\t]+)[\s\t]+of[\s\t]+([^\s\t]+)[\s\t]+is.*/);
			Pattern p5 = Pattern.compile(/(?:(?:^(?:component))|(?:^[\s\t]*(?:(?:[^-].*)|(?:-[^-]+.*)|(?:-$))(?:component)))[\s\t]+([^\s\t]+).*/);
			
			fileContents.replace('\r','').tokenize('\n').each {
				// prepare for matching, remove comments
				it = it.trim();
				if (it.startsWith("--")) return
				def commentStart = it.indexOf("--")
				if (commentStart >= 0) it = it.substring(0, commentStart)

				Matcher matcher = p1.matcher(it)
				if (matcher.matches()) {
					if (!matcher[0][1].toLowerCase().equals("ieee")) {
						String matching = matcher[0][2].toLowerCase();
						if (!definesUnits[file].contains(matching) && !dependsOn[file].contains(matching)) {
							dependsOn[file].add(matching);
						}
					}
				}

				matcher = p2.matcher(it)
				if (matcher.matches()) {
					String matching = matcher[0][2].toLowerCase();
					if (!definesUnits[file].contains(matching) && !dependsOn[file].contains(matching)) {
						dependsOn[file].add(matching);
					}
				}

				matcher = p3.matcher(it)
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

				matcher = p4.matcher(it)
				if (matcher.matches()) {
					String matching = matcher[0][1].toLowerCase();
					if (!definesUnits[file].contains(matching) && !dependsOn[file].contains(matching)) {
						dependsOn[file].add(matching);
					}
				}

				matcher = p5.matcher(it)
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