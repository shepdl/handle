package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TopicModelsList extends TreeView<TopicModel> {

    private ObjectProperty<TopicModel> selectedTopicModel;
    private ObservableList<TopicModel> topicModels;

    private final TreeItem rootItem = new TreeItem("Topic Models");

    private ContextMenu modelClickMenu;

    public TopicModelsList () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TopicModelsList.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // TODO: correct this so that it sets the event handler correctly
        this.setCellFactory(new Callback<TreeView<TopicModel>, TreeCell<TopicModel>>() {
            @Override
            public TreeCell<TopicModel> call(TreeView<TopicModel> param) {
                return new TopicModelCell();
            }
        });
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
                } else if (c.wasRemoved()) {
                    // NOTE: this is really inefficient as it's a linear search of a list multiple
                    // times, but there are unlikely to be enough topic models for this to be a problem
                    List<TopicModel> removedTopicModels = new ArrayList<>(c.getRemoved());
                    List<TopicModel> cellsToRemove = rootItem.getChildren().filtered((object) -> {
                        TreeItem<TopicModel> cell = (TreeItem<TopicModel>) object;
                        return removedTopicModels.contains(cell.getValue());
                    });
                    boolean status = rootItem.getChildren().removeAll(cellsToRemove);
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
                if (newValue == null) {
                    return;
                }
                TreeItem<TopicModel> selectedValue = (TreeItem<TopicModel>) newValue;
                selectedTopicModel.set(selectedValue.getValue());
            }
        });
    }

    private class TopicModelCell extends TextFieldTreeCell<TopicModel> {

        private class StringConverter extends javafx.util.StringConverter<TopicModel> {

            @Override
            public String toString(TopicModel topicModel) {
                return topicModel.getTitle();
            }

            @Override
            public TopicModel fromString(String s) {
                return item;
            }
        }

        private TopicModel item;

        public TopicModelCell () {
            super(new javafx.util.StringConverter<TopicModel>() {
                private TopicModel model;
                @Override
                public String toString(TopicModel topicModel) {
                    model = topicModel;
                    return topicModel.getTitle();
                }

                @Override
                public TopicModel fromString(String s) {
                    model.setTitle(s);
                    return model;
                }
            });
        }

        @Override
        public void updateItem (TopicModel item, boolean empty) {
            this.item = item;
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                if (item.getTitle() != null) {
                    stringBuilder.append(item.getTitle());
                    stringBuilder.append(" - (");
                }
                stringBuilder.append(item.getNumTopics());
                stringBuilder.append(" topics, ");
                stringBuilder.append("alpha " + item.getAlphaSum());
                stringBuilder.append(", beta " + item.getBeta());
                if (item.getTitle() != null) {
                    stringBuilder.append(")");
                }
                setText(stringBuilder.toString());

                ContextMenu modelClickMenu = new ContextMenu();
                MenuItem renameItem = new MenuItem("Rename");
                renameItem.setOnAction((event) -> startEdit());
                onEditCommitProperty().addListener((ev) -> {
                    this.updateItem(item, empty);
                });

                MenuItem deleteItem = new MenuItem("Delete");
                deleteItem.setOnAction((event) -> {
                    topicModels.remove(item);
                });

                modelClickMenu.getItems().addAll(renameItem, deleteItem);
                setContextMenu(modelClickMenu);
            }
        }

        @Override
        public boolean equals (Object o) {
            if (!(o instanceof TopicModelCell)) {
                return false;
            }
            TopicModelCell cell = (TopicModelCell)o;
            if (cell.item == null) {
                return item == cell.item;
            }
            return cell.item.equals(item);
        }
    }
}
