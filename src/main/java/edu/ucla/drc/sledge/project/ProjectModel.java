package edu.ucla.drc.sledge.project;

import cc.mallet.pipe.Pipe;
import cc.mallet.topics.TopicModel;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.documentimport.ImportPipeBuilder;
import edu.ucla.drc.sledge.documents.Document;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

public class ProjectModel {

    private String name;

    public ObservableList<Document> getDocuments() {
        return documents;
    }

    private ObservableList<Document> documents = FXCollections.observableArrayList();

    public ImportFileSettings getImportFileSettings() {
        return importFileSettings;
    }

    private InstanceList instances;

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
//            documents.get(i).setIngested(instances.get(i));
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

    public Pipe getPipe () {
        ImportPipeBuilder builder = new ImportPipeBuilder();
        builder.addSettings(importFileSettings);
        builder.addStopwords(new ArrayList<>(stopwords));
        return builder.complete();
    }

    private Pipe getFeaturePipe() {
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
//            documents.get(i).setIngested(instances.get(i));
        }
        return instances;
    }

    private ObservableList<TopicModel> topicModels = FXCollections.observableArrayList();

    public ObservableList<TopicModel> getTopicModels () {
        return topicModels;
    }

    public static ProjectModel blank () {
        return new ProjectModel();
    }

    public ProjectExportBuilder export () {
        return new ProjectExportBuilder(
                name, documents, importFileSettings, instances, stopwords, topicModels
        );
    }

    public static ProjectModel fromBuilder (ProjectExportBuilder builder) {
        ProjectModel model = new ProjectModel();
        return model;
    }

}
