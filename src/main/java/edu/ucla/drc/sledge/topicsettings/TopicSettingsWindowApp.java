package edu.ucla.drc.sledge.topicsettings;

import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.TopicTrainingJob;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class TopicSettingsWindowApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TopicTrainingJob job = TopicTrainingJob.createBlank();
        ProjectModel model = ProjectModel.blank();
        model.getDocuments().addAll(
            new Document(new File("./sample-data/59390-0.txt")),
            new Document(new File("./sample-data/59486-0.txt")),
            new Document(new File("./sample-data/pg59485.txt")),
            new Document(new File("./sample-data/pg59489.txt"))
        );
        TopicSettingsWindowController controller = new TopicSettingsWindowController(model, job);
        controller.initialize();

        primaryStage.show();
    }
}
