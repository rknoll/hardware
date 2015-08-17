package at.rknoll.gradle.hardware.compiler.quartus.tasks

class QuartusAsmTask extends BaseQuartusTask {
    public final static String TASK_NAME = "quartus_asm"

    QuartusAsmTask() {
        super(TASK_NAME)
        setDescription "Place and Route with Quartus."
    }
}
