package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.importsettings.ImportSettingsController;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

import java.io.File;

public class DocumentListController {

    private final ObservableList<Document> documents;
    private final File storageRoot;
    private WritableObjectValue<Document> selectedDocument;

    public DocumentListController(ObservableList<Document> documents, File storageRoot, WritableObjectValue<Document> selectedDocument) {
        this.documents = documents;
        this.storageRoot = storageRoot;
        this.selectedDocument = selectedDocument;
    }

    public void initialize (VBox parent) {
        DocumentListView listView = new DocumentListView(documents, selectedDocument);
        listView.initialize(parent);
    }
}
