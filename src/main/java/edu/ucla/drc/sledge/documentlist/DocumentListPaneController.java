package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.ImportFileSettings;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;

public class DocumentListPaneController {

    private final ObservableList<Document> documents;
    private final File projectStorageRoot;
    private final ImportFileSettings settings;
    private WritableObjectValue<Document> selectedDocument;

    public DocumentListPaneController (ObservableList<Document> documents, File projectStorageRoot, ImportFileSettings settings, WritableObjectValue<Document> selectedDocument) {
        this.documents = documents;
        this.projectStorageRoot = projectStorageRoot;
        this.settings = settings;
        this.selectedDocument = selectedDocument;
    }

    public void initialize (HBox parent) {
        VBox root = new VBox();

        DocumentListController listController = new DocumentListController(documents, projectStorageRoot, selectedDocument);
        listController.initialize(root);

        DocumentListSettingsButtonsController settingsButtonsController = new DocumentListSettingsButtonsController(settings);
        settingsButtonsController.initialize(root);

        parent.getChildren().add(root);
    }
}
