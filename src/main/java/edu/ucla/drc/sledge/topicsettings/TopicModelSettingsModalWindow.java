package edu.ucla.drc.sledge.topicsettings;

import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.topicmodel.TopicModelResults;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class TopicModelSettingsModalWindow {
    private final Consumer<TopicModelResults> callback;
    private final ProjectModel model;

    @FXML private TopicModelSettings settingsPane;
    private TopicModelResults topicModel;
    private Stage window;

    // Contains the new stage and scene
    // Constructed by parent window and then calls a callback on closing
    
    public TopicModelSettingsModalWindow (Consumer<TopicModelResults> closeCallback, ProjectModel model) {
        this.callback = closeCallback;
        this.model = model;
    }

    public void show () {
        // Display stage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TopicModelSettingsModalWindow.fxml"));
        this.window = new Stage();
//        loader.setRoot(this);
        loader.setController(this);
//        try {
//            this.window.initModality(Modality.APPLICATION_MODAL);
//            this.window.setTitle("New topic model");
//            loader.load();
//            settingsPane.setup(this.model);
//            this.window.show();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    @FXML
    public void initialize () {
        settingsPane.setup(this.model);
    }

    public void closeButtonHandler (MouseEvent event) {
        callback.accept(topicModel);
    }

}
