package edu.ucla.drc.sledge.topicsettings;

import edu.ucla.drc.sledge.topicmodel.TopicModelResults;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TopicModelSettingsApp extends Application {

    public void callback (TopicModelResults model) {

    }

    @Override
    public void start(Stage stage) throws Exception {
        TopicModelSettingsModalWindow window = new TopicModelSettingsModalWindow(this::callback, null);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("TopicModelSettingsModalWindow.fxml"));
        loader.setController(window);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }


}
