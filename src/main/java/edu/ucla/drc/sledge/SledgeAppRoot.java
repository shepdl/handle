package edu.ucla.drc.sledge;

import edu.ucla.drc.sledge.topicmodeling.TopicModelsTab;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class SledgeAppRoot extends AnchorPane {

    @FXML private DocumentImport documentImport;
    @FXML private TopicModelsTab topicModels;

    @FXML
    public void initialize () {

        ProjectModel model = ProjectModel.blank();
//        documentImport.setData(model.getDocuments(), selectedDocument);
        documentImport.setModel(model);
        topicModels.setModel(model);

    }
}
