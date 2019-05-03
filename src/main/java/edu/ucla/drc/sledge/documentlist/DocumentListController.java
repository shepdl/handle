package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.ImportFileSettings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

import java.io.File;

public class DocumentListController {

    private final ObservableList<Document> documents;
    private final File storageRoot;
    private ObservableValue<Document> selectedDocument;

    public DocumentListController(ObservableList<Document> documents, File storageRoot, ObservableValue<Document> selectedDocument) {
        this.documents = documents;
        this.storageRoot = storageRoot;
        this.selectedDocument = selectedDocument;
    }

    public void initialize (VBox parent) {
        DocumentListView listView = new DocumentListView(documents, selectedDocument);
        listView.initialize(parent);
    }
}
