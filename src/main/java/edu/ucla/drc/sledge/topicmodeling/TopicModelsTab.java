package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.LoadsFxml;
import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.topicsettings.TopicModelSettings;
import edu.ucla.drc.sledge.topicsettings.TopicModelSettingsModalWindow;
import edu.ucla.drc.sledge.topicsettings.TopicSummary;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TopicModelsTab extends BorderPane implements LoadsFxml {

    @FXML private TopicModelsList topicModelsList;
    @FXML private Button addTopicModelButton;
    @FXML private ModelVisualization topicModelSettings;

    private ProjectModel model;
    private SimpleObjectProperty<TopicModel> selectedTopicModel = new SimpleObjectProperty<>();

    private ObservableList<TopicModel> topicModels = FXCollections.observableArrayList();

    public TopicModelsTab () {
        loadFxml();
    }

    public void setModel (ProjectModel model) {
        this.model = model;
        topicModelSettings.setProjectModel(model);
        topicModelsList.setData(topicModels, selectedTopicModel);
//        topicModelSettings.setup(model);
        topicModelsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        topicModelsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TopicModel>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<TopicModel>> observableValue, TreeItem<TopicModel> oldValue, TreeItem<TopicModel> newValue) {
                topicModelSettings.setVisible(true);
                topicModelSettings.setTopicModel(newValue.getValue());
            }
        });
        selectedTopicModel.addListener(new ChangeListener<TopicModel>() {
            @Override
            public void changed(ObservableValue<? extends TopicModel> observable, TopicModel oldValue, TopicModel newValue) {
//                topicModelSettings.setTopicModel(newValue);
//                topicModelSettings.setTopicModel(newValue);
            }
        });
    }

    public void addTopicModel (MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TopicModelSettingsModalWindow.fxml"));
        try {
            TopicModelSettingsModalWindow controller = new TopicModelSettingsModalWindow(this::topicModelComplete, model);
            loader.setController(controller);
            Parent root = (Parent) loader.load();
            Scene scene = new Scene(root, 400, 800);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New topic model");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void topicModelComplete(TopicModel model) {
        topicModels.add(model);
    }

}
