package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DocumentViewerView {
    private ObjectProperty<Document> selectedDocument;

    public DocumentViewerView(ObjectProperty<Document> selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public void initialize(HBox parent) {
        VBox root = new VBox();
        parent.getChildren().add(root);
    }
}
