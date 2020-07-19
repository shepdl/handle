package edu.ucla.drc.sledge.project;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.documents.Document;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectExportBuilder implements Serializable {

    private static final long serialVersionUID = 1;

    @JsonProperty("name")
    private String name;

    @JsonProperty("documents")
    private List<Document.Exporter> documents;

    @JsonProperty("importFileSettings")
    private ImportFileSettings importFileSettings;

    @JsonProperty("instances")
    private InstanceList instances;

    @JsonProperty("stopwords")
    private Set<String> stopwords;

    @JsonProperty("topicModels")
    private List<TopicModel.TopicModelSettingsExporter> topicModels;

    public ProjectExportBuilder() {

    }

    public ProjectExportBuilder(List<Document>documents,
            ImportFileSettings importFileSettings, Set<String> stopwords,
            List<TopicModel> topicModels
    ) {
//        this.documents = documents;
        this.documents = documents.stream().map((doc) -> {
            // TODO: fix
            DocExporterToProjectFile exporter = new DocExporterToProjectFile();
            try {
                doc.exportTo(exporter);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return exporter;
//            return new DocumentExporter(doc.getFile());
//            return new DocumentExporter(doc.getFile(), doc.getIngested());
        }).collect(Collectors.toList());
        this.importFileSettings = importFileSettings;
        this.stopwords = stopwords;
        this.topicModels = topicModels.stream().map((model) -> {
            return new TopicModel.TopicModelSettingsExporter(model);
        }).collect(Collectors.toList());
//        this.topicModels = new ArrayList<>(topicModels);
    }

    public ProjectModel toModel () {
        ProjectModel model = new ProjectModel();
        model.setStopwords(stopwords);
        return model;
    }

    private static class DocExporterToProjectFile implements Document.Exporter {

        @JsonProperty("content")
        private String content;

        @JsonProperty("name")
        private String name;

        @JsonProperty("uri")
        private String uri;

        @Override
        public void addContent(String content) {
            this.content = content;
        }

        @Override
        public void addName(String name) {
            this.name = name;
        }

        @Override
        public void addUri(URI uri) {
            this.uri = uri.toString();
        }

        @Override
        public void addFile(File file) {
            return;
        }

    }

}
