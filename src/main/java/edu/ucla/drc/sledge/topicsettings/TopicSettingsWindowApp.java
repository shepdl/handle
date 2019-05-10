package edu.ucla.drc.sledge.topicsettings;

import edu.ucla.drc.sledge.TopicTrainingJob;
import javafx.application.Application;
import javafx.stage.Stage;

public class TopicSettingsWindowApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TopicTrainingJob job = TopicTrainingJob.createBlank();
        TopicSettingsWindowController controller = new TopicSettingsWindowController(job);
        controller.initialize();

        primaryStage.show();
    }
}
