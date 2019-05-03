package edu.ucla.drc.sledge;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class ProjectModel {

    private String name;

    public File getStorageRoot() {
        return storageRoot;
    }

    private File storageRoot;

    public ObservableList<Document> getDocuments() {
        return documents;
    }

    private ObservableList<Document> documents = FXCollections.observableArrayList();

    public ImportFileSettings getImportFileSettings() {
        return importFileSettings;
    }

    private ImportFileSettings importFileSettings;

    private ObservableList topicJobs;

    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract(value = " -> new", pure = true)
    public static ProjectModel blank () {
        return new ProjectModel();
    }

}
