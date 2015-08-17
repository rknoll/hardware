package at.rknoll.gradle.hardware.language.vhdl

import at.rknoll.gradle.hardware.HardwarePluginConvention
import at.rknoll.parser.vhdl.VhdlBaseListener
import at.rknoll.parser.vhdl.VhdlLexer
import at.rknoll.parser.vhdl.VhdlParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VhdlFindDependenciesTask extends DefaultTask {

    @TaskAction
    def compile() {
        def dependsOn = new HashMap<File, List<String>>()
        def definesUnits = new HashMap<File, List<String>>()
        def convention = project.convention.getPlugin HardwarePluginConvention

        for (File file : convention.hardwareSources.vertexSet()) {
            if (!VhdlUtils.isVhdlFile(file)) continue

            println "analyzing " + file.name + "..."

            // construct lexer and parsers
            def stream = new ANTLRInputStream(file.text)
            def lexer = new VhdlLexer(stream)
            def tokenStream = new CommonTokenStream(lexer)
            def parser = new VhdlParser(tokenStream)

            // parse design file
            def designFile = parser.design_file()

            dependsOn[file] = []
            definesUnits[file] = []

            def listener = new VhdlBaseListener() {
                def defines(String identifier) {
                    identifier = identifier.toLowerCase()
                    if (!definesUnits[file].contains(identifier)) {
                        definesUnits[file].add(identifier)
                    }
                    dependsOn[file].remove(identifier)
                }

                def depends(String identifier) {
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
                            def identifier = selected.suffix(0).identifier()
                            depends(identifier != null ? identifier.text : selected.identifier().text)
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
                    def suffix = ctx.name().selected_name().suffix(0)
                    def identifier = suffix != null ? suffix.identifier() : ctx.name().selected_name().identifier()
                    depends(identifier.text)
                }
            }

            def walker = new ParseTreeWalker()
            walker.walk(listener, designFile)
        }

        for (File file : convention.hardwareSources.vertexSet()) {
            if (!VhdlUtils.isVhdlFile(file)) continue
            println file.name + ": " + definesUnits[file] + "->" + dependsOn[file]
            dependsOn[file].each { depId ->
                definesUnits.each { defFile, unitList ->
                    if (unitList.contains(depId)) {
                        if (!convention.hardwareSources.containsEdge(defFile, file)) {
                            convention.hardwareSources.addEdge(defFile, file)
                        }
                    }
                }
            }
        }
    }

}
