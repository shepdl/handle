package edu.ucla.drc.sledge.project;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.TopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.documentimport.ImportPipeBuilder;
import edu.ucla.drc.sledge.documents.CsvDocumentIterator;
import edu.ucla.drc.sledge.documents.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class ProjectModel {

    public ObservableList<Document> getDocuments() {
        return documents;
    }

    private ObservableList<Document> documents = FXCollections.observableArrayList();

    public ImportFileSettings getImportFileSettings() {
        return importFileSettings;
    }

    private ImportFileSettings importFileSettings = ImportFileSettings.withDefaults();

    public Set<String> getStopwords() {
        return stopwords;
    }

    public void addStopword(String word) {
        stopwords.add(word);
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
        Iterator<Instance> iterator;
        if (importFileSettings.getIterationSchema() == ImportFileSettings.DocumentIterationSchema.ONE_DOC_PER_FILE) {
            iterator = new DocumentIterator(documents);
        } else {
            iterator = new CsvDocumentIterator(documents);
        }
        instances.addThruPipe(iterator);
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
        return new ProjectExportBuilder(documents, importFileSettings, stopwords, topicModels);
    }

    public static ProjectModel fromBuilder (ProjectExportBuilder builder) {
        ProjectModel model = new ProjectModel();
        return model;
    }

}
