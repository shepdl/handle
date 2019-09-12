package edu.ucla.drc.sledge.topicsettings;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.ProjectModel;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class TopicModelSettingsModalWindow {
    private final Consumer<TopicModel> callback;
    private final ProjectModel model;

    @FXML private TopicModelSettings settingsPane;
    private Stage window;

    // Contains the new stage and scene
    // Constructed by parent window and then calls a callback on closing
    
    public TopicModelSettingsModalWindow (Consumer<TopicModel> closeCallback, ProjectModel model) {
        this.callback = closeCallback;
        this.model = model;
    }

    @FXML
    public void initialize () {
        settingsPane.setup(this.model, this.callback);
    }
    public void closeButtonHandler (MouseEvent event) {
    }

}
