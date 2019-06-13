package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicAssignment;
import cc.mallet.topics.TopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.LabelAlphabet;
import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.topicsettings.TopicModelSettingsWindow;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.ArrayList;

public class TopicModelsTab extends BorderPane {

    @FXML private TopicModelsList topicModelsList;
    @FXML private Button addTopicModelButton;
    @FXML private TopicModelSettingsWindow topicModelSettings;

    private ProjectModel model;

    private ObservableList<TopicModel> topicModels = FXCollections.observableArrayList();

    public TopicModelsTab () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TopicModelsTab.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setModel (ProjectModel model) {
        this.model = model;
        SimpleObjectProperty<TopicModel> selectedTopicModel = new SimpleObjectProperty<>();
        topicModelsList.setData(topicModels, selectedTopicModel);
        topicModelSettings.setup(model);
        selectedTopicModel.addListener(new ChangeListener<TopicModel>() {
            @Override
            public void changed(ObservableValue<? extends TopicModel> observable, TopicModel oldValue, TopicModel newValue) {
                topicModelSettings.setTopicModel(newValue);
            }
        });
    }

    public void addTopicModel (MouseEvent event) {
        TopicModel model = new TopicModel(20);
        topicModels.add(model);
    }

}
