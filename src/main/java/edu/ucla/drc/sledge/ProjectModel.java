package edu.ucla.drc.sledge;

import cc.mallet.pipe.Pipe;
import cc.mallet.topics.TopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.documentimport.ImportPipeBuilder;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.*;

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

    public InstanceList getInstances() {
        return instances;
    }

    private InstanceList instances;

    private static class DocumentIterator implements Iterator<Instance> {

        private final List<Document> documents;
        private int index = 0;

        public DocumentIterator(List<Document> documents) {
            this.documents = documents;
        }

        @Override
        public boolean hasNext() {
            return index < documents.size();
        }

        @Override
        public Instance next() {
            Document document = documents.get(index);
            index++;
            URI uri = document.getFile().toURI();
            try {
                return new Instance(
                        document.getTextContent(), null, uri, null
                );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public ProjectModel () {
        documents.addListener(new ListChangeListener<Document>() {
            @Override
            public void onChanged(Change<? extends Document> c) {
                reimportDocuments();
            }
        });
    }

    private void reimportDocuments () {
        instances = new InstanceList(getPipe());
        DocumentIterator iterator = new DocumentIterator(documents);
        instances.addThruPipe(iterator);
        for (int i = 0; i < documents.size(); i++) {
            documents.get(i).setIngested(instances.get(i));
        }
    }

    private ImportFileSettings importFileSettings = ImportFileSettings.withDefaults();

    public Set<String> getStopwords() {
        return stopwords;
    }

    public void addStopword(String word) {
        stopwords.add(word);
        reimportDocuments();
    }

    public void setStopwords(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    private Set<String> stopwords = new HashSet<>();

    private Pipe importPipe;

    public Pipe getPipe () {
        ImportPipeBuilder builder = new ImportPipeBuilder();
        builder.addSettings(importFileSettings);
        builder.addStopwords(new ArrayList<>(stopwords));
        return builder.complete();
    }

    public Pipe getFeaturePipe () {
        ImportPipeBuilder builder = new ImportPipeBuilder();
        builder.addSettings(importFileSettings);
        builder.addStopwords(new ArrayList<>(stopwords));
        return builder.buildFeaturePipe();
    }

    public InstanceList getInstancesForModeling () {
        InstanceList instances = new InstanceList(getFeaturePipe());
        DocumentIterator iterator = new DocumentIterator(documents);
        instances.addThruPipe(iterator);
        for (int i = 0; i < documents.size(); i++) {
            documents.get(i).setIngested(instances.get(i));
        }
        return instances;
    }

    private ObservableList<TopicModel> topicModels = FXCollections.observableArrayList();

    public ObservableList<TopicModel> getTopicModels () {
        return topicModels;
    }

    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract(value = " -> new", pure = true)
    public static ProjectModel blank () {
        return new ProjectModel();
    }

}
