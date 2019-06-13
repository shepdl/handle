package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.topicsettings.Topic;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.IOException;

public class TopicModelsList extends TreeView<TopicModel> {

    private ObjectProperty<TopicModel> selectedTopicModel;
    private ObservableList<TopicModel> topicModels;

    private final TreeItem rootItem = new TreeItem("Topic Models");

    public TopicModelsList () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TopicModelsList.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData (ObservableList<TopicModel> topicModels, ObjectProperty<TopicModel> selectedTopicModel) {
        this.topicModels = topicModels;
        this.selectedTopicModel = selectedTopicModel;

        this.topicModels.addListener((ListChangeListener.Change<? extends TopicModel> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(topicModel -> {
                        TreeItem<TopicModel> treeItem = new TreeItem<>(topicModel);
                        rootItem.getChildren().add(treeItem);
                    });
                }
            }
        });
    }

    @FXML
    public void initialize () {
        this.setRoot(rootItem);
        this.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TopicModel>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<TopicModel>> observable, TreeItem<TopicModel> oldValue, TreeItem<TopicModel> newValue) {
                TreeItem<TopicModel> selectedValue = (TreeItem<TopicModel>) newValue;
                selectedTopicModel.set(selectedValue.getValue());
            }
        });
    }
}
