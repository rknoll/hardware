package at.rknoll.utils

import org.gradle.api.Project

public class ExecUtils {
    public static void exec(Project project, args) {
        exec project, args, null
    }

    public static void exec(Project project, args, workingDir) {
        new ByteArrayOutputStream().withStream { os ->
            def result = project.exec {
                it.commandLine = args
                it.standardOutput = os
                it.ignoreExitValue = true
                if (workingDir != null) it.workingDir = workingDir.getAbsolutePath()
            }

            def output = os.toString()
            def exitCode = result.getExitValue()

            if (exitCode != 0 || output.contains("ERROR at line")) {
                throw new RuntimeException("Error " + exitCode + " while executing '" +
                        args.join(" ") + "'\noutput:\n" + output);
            }
        }
    }
}
