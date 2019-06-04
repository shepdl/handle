package edu.ucla.drc.sledge.topicsettings;

import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.TopicTrainingJob;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class TopicSettingsWindowAppFxml extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TopicTrainingJob job = TopicTrainingJob.createBlank();
        ProjectModel model = ProjectModel.blank();
        File sourceDir = new File("./sample-data/");
        File[] sourceFiles = sourceDir.listFiles();
        for (int i = 0; i < sourceFiles.length; i++) {
            model.getDocuments().add(
                    new Document(sourceFiles[i])
            );
        }
//        TopicSettingsWindowController controller = new TopicSettingsWindowController(model, job);
//        controller.initialize();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("TopicModelSettingsWindow.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("TopicModelSettingsWindow.fxml"));
        Parent root = loader.getRoot();
        Scene scene = new Scene(loader.load(), 800, 600);
        primaryStage.setScene(scene);

        primaryStage.show();
        TopicModelSettingsWindow controller = loader.<TopicModelSettingsWindow>getController();
        controller.setup(model);
    }
}
