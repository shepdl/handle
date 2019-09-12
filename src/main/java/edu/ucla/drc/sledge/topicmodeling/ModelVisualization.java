package edu.ucla.drc.sledge.topicmodeling;

import edu.ucla.drc.sledge.topicmodel.TopicModelResults;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class ModelVisualization {
    private TopicModelResults model;

    public void exportModel(MouseEvent mouseEvent) {
        // TODO: write to a file
    }

    public void exportTopics(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select output");
//        File file = fileChooser.showSaveDialog();
//        if (file != null) {
//             Draw image
//        }
    }

    public void setData (TopicModelResults model) {
        this.model = model;
    }
}
