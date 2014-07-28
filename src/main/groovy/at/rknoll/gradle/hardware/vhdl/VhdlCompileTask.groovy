package at.rknoll.gradle.hardware.vhdl

import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class VhdlCompileTask extends SourceTask {

    @TaskAction
    def compile() {
        println "-- Vhdl Compile --"
    }

}
