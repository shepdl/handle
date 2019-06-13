package edu.ucla.drc.sledge;

import edu.ucla.drc.sledge.topicmodeling.TopicModelsTab;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class SledgeAppRoot extends AnchorPane {

    @FXML DocumentImport documentImport;
    @FXML TopicModelsTab topicModels;

    @FXML
    public void initialize () {

        ProjectModel model = ProjectModel.blank();
//        documentImport.setData(model.getDocuments(), selectedDocument);
        documentImport.setModel(model);
        topicModels.setModel(model);

    }
}
