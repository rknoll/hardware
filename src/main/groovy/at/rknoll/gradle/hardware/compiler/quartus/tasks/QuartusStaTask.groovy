package at.rknoll.gradle.hardware.compiler.quartus.tasks

import at.rknoll.gradle.hardware.compiler.quartus.QuartusPlugin

/**
 * Created by Richard on 30.03.2015.
 */
class QuartusStaTask extends BaseQuartusTask {
    public final static String TASK_NAME = "quartus_sta"

    QuartusStaTask() {
        super("quartus_sta")
        setDescription("Timing Analysis with Quartus.");
        setGroup(QuartusPlugin.SYNTHESIZE_GROUP)
    }
}
