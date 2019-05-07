package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.HBox;

public class WordCountTableController {
    private ObjectProperty<Document> selectedDocument;

    public WordCountTableController(ObjectProperty<Document> selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public void initialize(HBox root) {
        WordCountTableView view = new WordCountTableView(selectedDocument);
        view.initialize(root);
    }
}
