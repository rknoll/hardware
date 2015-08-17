package at.rknoll.gradle.hardware.compiler.quartus.tasks

class QuartusFitTask extends BaseQuartusTask {
    public final static String TASK_NAME = "quartus_fit"

    QuartusFitTask() {
        super(TASK_NAME)
        setDescription "Fitting with Quartus."
    }
}
