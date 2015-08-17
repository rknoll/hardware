package at.rknoll.gradle.hardware.compiler.quartus.tasks

class QuartusStaTask extends BaseQuartusTask {
    public final static String TASK_NAME = "quartus_sta"

    QuartusStaTask() {
        super(TASK_NAME)
        setDescription "Timing Analysis with Quartus."
    }
}
