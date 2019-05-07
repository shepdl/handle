package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.HBox;

public class DocumentTextViewerController {

    private ObjectProperty<Document> selectedDocument;

    public DocumentTextViewerController(ObjectProperty<Document> selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public void initialize (HBox parent) {
        DocumentTextViewerView view = new DocumentTextViewerView(selectedDocument);
        view.initialize(parent);
    }
}
