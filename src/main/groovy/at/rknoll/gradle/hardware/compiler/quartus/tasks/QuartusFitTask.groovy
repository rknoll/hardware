package at.rknoll.gradle.hardware.compiler.quartus.tasks

import at.rknoll.gradle.hardware.compiler.quartus.QuartusPlugin

/**
 * Created by Richard on 30.03.2015.
 */
class QuartusFitTask extends BaseQuartusTask {
    public final static String TASK_NAME = "quartus_fit"

    QuartusFitTask() {
        super("quartus_fit")
        setDescription "Fitting with Quartus."
        setGroup QuartusPlugin.SYNTHESIZE_GROUP
    }
}
