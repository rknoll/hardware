package at.rknoll.gradle.hardware.compiler.quartus.tasks

import at.rknoll.gradle.hardware.compiler.quartus.QuartusPlugin

/**
 * Created by Richard on 30.03.2015.
 */
class QuartusAsmTask extends BaseQuartusTask {
    public final static String TASK_NAME = "quartus_asm"

    QuartusAsmTask() {
        super("quartus_asm")
        setDescription("Place and Route with Quartus.");
        setGroup(QuartusPlugin.SYNTHESIZE_GROUP)
    }
}
