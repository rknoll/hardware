package at.rknoll.gradle.hardware.language.vhdl

import at.rknoll.parser.vhdl.VhdlBaseListener
import at.rknoll.parser.vhdl.VhdlLexer
import at.rknoll.parser.vhdl.VhdlParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VhdlFindDependenciesTask extends DefaultTask {

    @TaskAction
    def compile() {
        def dependsOn = [:]
        def definesUnits = [:]

        for (File file : project.hardwareSources.vertexSet()) {
            if (!VhdlUtils.isVhdlFile(file)) continue

            // construct lexer and parsers
            def stream = new ANTLRInputStream(file.text)
            def lexer = new VhdlLexer(stream)
            def tokenStream = new CommonTokenStream(lexer)
            def parser = new VhdlParser(tokenStream)

            // parse design file
            def designFile = parser.design_file()

            dependsOn[file] = []
            definesUnits[file] = []

            def visitor = new VhdlBaseListener() {
                def defines(identifier) {
                    identifier = identifier.toLowerCase()
                    if (!definesUnits[file].contains(identifier)) {
                        definesUnits[file].add(identifier)
                    }
                    dependsOn[file].remove(identifier)
                }

                def depends(identifier) {
                    identifier = identifier.toLowerCase()
                    if (!dependsOn[file].contains(identifier) && !definesUnits[file].contains(identifier)) {
                        dependsOn[file].add(identifier)
                    }
                }

                @Override
                void enterEntity_declaration(VhdlParser.Entity_declarationContext ctx) {
                    defines(ctx.identifier(0).text)
                }

                @Override
                void enterPackage_declaration(VhdlParser.Package_declarationContext ctx) {
                    defines(ctx.identifier(0).text)
                }

                @Override
                void enterUse_clause(VhdlParser.Use_clauseContext ctx) {
                    for (def selected : ctx.selected_name()) {
                        if (!selected.identifier().text.toLowerCase().equals("ieee")) {
                            depends(selected.suffix(0).identifier().text)
                        }
                    }
                }

                @Override
                void enterArchitecture_body(VhdlParser.Architecture_bodyContext ctx) {
                    depends(ctx.identifier(1).text)
                }

                @Override
                void enterPackage_body(VhdlParser.Package_bodyContext ctx) {
                    depends(ctx.identifier(0).text)
                }

                @Override
                void enterInstantiated_unit(VhdlParser.Instantiated_unitContext ctx) {
                    depends(ctx.name().selected_name().suffix(0).identifier().text)
                }
            }

            println "analyzing " + file.name + "..."

            visitor.visit(designFile)
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
