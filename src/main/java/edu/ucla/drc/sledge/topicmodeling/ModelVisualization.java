package edu.ucla.drc.sledge.topicmodeling;

import edu.ucla.drc.sledge.topicmodel.TopicModel;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.TreeSet;

public class ModelVisualization {
    private TopicModel model;

    public void exportModel(MouseEvent mouseEvent) {
        // TODO: write them to the database
    }

    public void exportTopics(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select output");
        File file = fileChooser.showSaveDialog();
        if (file != null) {
            // Draw image
            List<TreeSet> set = model.getSortedWords();
            for (int i = 0; i < model.getNumTopics(); i++) {
            }
        }
    }

    public void setData (TopicModel model) {
        this.model = model;
    }
}
