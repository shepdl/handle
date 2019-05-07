package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.HBox;

public class DocumentViewerController {
    private ObjectProperty<Document> selectedDocument;

    public DocumentViewerController(ObjectProperty<Document> selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public void initialize(HBox parent) {
        HBox root = new HBox();
        DocumentTextViewerController textController = new DocumentTextViewerController(selectedDocument);
        textController.initialize(root);

        WordCountTableController wordCountController = new WordCountTableController(selectedDocument);
        wordCountController.initialize(root);
        parent.getChildren().add(root);
    }

}
