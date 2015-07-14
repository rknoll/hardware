package at.rknoll.gradle.hardware.language.soc

import at.rknoll.gradle.hardware.HardwarePlugin
import at.rknoll.gradle.hardware.language.soc.ui.HardwareDialog
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by rknoll on 21/04/15.
 */
class HardwareUiTask extends DefaultTask {
    public static final String HARDWARE_UI_TASK_NAME = "hardwareUI";
    public static final String UI_GROUP_NAME = "UI";

    HardwareUiTask() {
        setDescription("Show the Hardware UI Window.");
        setGroup(UI_GROUP_NAME);
        dependsOn(HardwarePlugin.PREPARE_TASK_NAME);
    }

    @TaskAction
    def show() {
        HardwareDialog dialog = new HardwareDialog();
        dialog.pack();
        dialog.setVisible(true);
    }
}
