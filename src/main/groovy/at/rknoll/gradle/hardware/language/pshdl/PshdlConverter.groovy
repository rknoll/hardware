package at.rknoll.gradle.hardware.language.pshdl

import org.gradle.api.Project
import org.gradle.process.ExecResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PshdlConverter {
    private Project project
    protected Logger logger
    private Map<String, ArrayList<String>> files

    public PshdlConverter(Project project) {
        this.project = project
        logger = LoggerFactory.getLogger('pshdl-logger')
        files = new HashMap<>()
    }

    public File prepareConvert(File file, File destDir) {
        if (!file.name.endsWith(".pshdl")) return null
        File destFile = new File(destDir, file.name.substring(0, file.name.length() - 5) + "vhdl")
        if (files[destDir.getAbsolutePath()] == null) {
            files[destDir.getAbsolutePath()] = new ArrayList<>()
        }
        files[destDir.getAbsolutePath()].add(file.getAbsolutePath())
        return destFile
    }

    public void convert() {
        if (files.isEmpty()) return

        def pshdlPath = PshdlUtils.findPshdlExecutable("pshdl.jar", project.pshdl as PshdlExtension)
        def args = ["java"]

        if (pshdlPath == null) {
            // no pshdl jar available, use the bundled version
            args += ["-cp", project.configurations.pshdl.asPath, "org.pshdl.commandline.PSHDLCompiler"]
        } else {
            args += ["-jar", pshdlPath]
        }

        files.each { entry ->
            File destDir = new File(entry.key)
            destDir.mkdirs()

            def line = args.clone()
            line += ["vhdl", "-o", destDir.getAbsolutePath()]
            entry.value.each { line.add(it) }

            new ByteArrayOutputStream().withStream { os ->
                ExecResult result = project.exec {
                    commandLine = line
                    standardOutput = os
                    ignoreExitValue = true
                }

                String output = os.toString()
                int exitCode = result.getExitValue()

                if (exitCode != 0 || output.contains("ERROR at line")) {
                    throw new RuntimeException("Error " + exitCode + " while executing '" + args.join(" ") + "'\noutput:\n" + output);
                }
            }
        }
    }
}
