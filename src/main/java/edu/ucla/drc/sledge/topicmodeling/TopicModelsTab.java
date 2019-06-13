package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.topicsettings.TopicModelSettings;
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

public class TopicModelsTab extends BorderPane {

    @FXML private TopicModelsList topicModelsList;
    @FXML private Button addTopicModelButton;
    @FXML private TopicModelSettings topicModelSettings;

    private ProjectModel model;
    private SimpleObjectProperty<TopicModel> selectedTopicModel = new SimpleObjectProperty<>();

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
        TopicModel model = new TopicModel(20, 50.0, 0.01);
        topicModels.add(model);
        selectedTopicModel.set(model);
    }

}
