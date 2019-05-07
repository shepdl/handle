package edu.ucla.drc.sledge;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.pipe.iterator.FileListIterator;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.documentlist.ImportPipeBuilder;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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

    private ObservableBooleanValue isImporting;

    public Boolean getIsImporting() {
        return isImporting.get();
    }

    private InstanceList instances;

    public ProjectModel () {
        documents.addListener(new ListChangeListener<Document>() {
            @Override
            public void onChanged(Change<? extends Document> c) {
                instances = new InstanceList(getPipe());
                ArrayList<String> inFiles = new ArrayList<String>();
                for (Document doc : documents) {
                    try {
                        inFiles.add(doc.getTextContent());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                instances.addThruPipe(new StringArrayIterator(inFiles.toArray(new String[0])));
                for (int i = 0; i < documents.size(); i++) {
                    documents.get(i).setIngested(instances.get(i));
                }
                System.out.println("Done");
            }
        });
    }

    private ImportFileSettings importFileSettings = ImportFileSettings.withDefaults();

    private Pipe importPipe;

    public Pipe getPipe () {
        ImportPipeBuilder builder = new ImportPipeBuilder();
        builder.addSettings(importFileSettings);
        return builder.complete();
    }

    private ObservableList topicJobs;

    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract(value = " -> new", pure = true)
    public static ProjectModel blank () {
        return new ProjectModel();
    }

}
