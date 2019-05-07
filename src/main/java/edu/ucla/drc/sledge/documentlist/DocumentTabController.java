package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.ProjectModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

public class DocumentTabController {

    private final Tab root;
    private final ProjectModel projectModel;
    private ObjectProperty<Document> selectedDocument;

    public DocumentTabController (Tab root, ProjectModel projectModel) {
        this.root = root;
        this.projectModel = projectModel;
    }

    public void initialize (Tab parent) {
        HBox root = new HBox();
        // Create document list pane controller
        selectedDocument = new SimpleObjectProperty<Document>();
        DocumentListPaneController listPaneController = new DocumentListPaneController(
                projectModel.getDocuments(),
                projectModel.getStorageRoot(),
                projectModel.getImportFileSettings(),
                selectedDocument
        );
        listPaneController.initialize(root);
        // Create document view pane controller

        DocumentViewerController viewerController = new DocumentViewerController(selectedDocument);
        viewerController.initialize(root);

        parent.setContent(root);
    }

}
