package edu.ucla.drc.sledge.project;

import cc.mallet.topics.TopicModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.documents.Document;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProjectModelBuilderFromJson implements ProjectModel.Importer {
    @Override
    public List<Document> provideDocuments() {
        return documents.stream().map((doc) -> {
            return new CachedSourceDocument(doc.name, doc.content, doc.uri);
        }).collect(Collectors.toList());
    }

    @Override
    public ImportFileSettings provideSettings() {
        return new ImportFileSettings(importFileSettings);
    }

    @Override
    public Set<String> provideStopwords() {
        return stopwords;
    }

    @Override
    public List<TopicModel> provideTopicModels() {
        return topicModels;
    }

    public ProjectModel toModel () {
        return new ProjectModel(this);
    }

    @JsonProperty("name")
    private String name;

    @JsonProperty("documents")
    private List<DocumentImporterFromJson> documents;

    @JsonProperty("importFileSettings")
    private ImportFileSettingsImporterFromJson importFileSettings;

    @JsonProperty("stopwords")
    private Set<String> stopwords;

    @JsonProperty("topicModels")
    private List<TopicModel> topicModels;

    private static class DocumentImporterFromJson implements Document.Importer {

        @JsonProperty("name")
        private String name;
        @JsonProperty("content")
        private String content;
        @JsonProperty("uri")
        private URI uri;
        @JsonProperty(value = "file", defaultValue = "missing")
        private File file;

        @Override
        public String provideContent() {
            return content;
        }

        @Override
        public String provideName() {
            return name;
        }

        @Override
        public URI provideUri() {
            return uri;
        }

        @Override
        public File provideFile() {
            return file;
        }
    }

    private static class ImportFileSettingsImporterFromJson implements ImportFileSettings.Importer {

        @JsonProperty("preserveCase")
        private boolean preserveCase;
        @JsonProperty("tokenRegex")
        private String tokenRegexPattern;
        @JsonProperty("keepSequence")
        private boolean keepSequenceBigrams;
        @JsonProperty("iterationSchema")
        private ImportFileSettings.DocumentIterationSchema iterationSchema;

        @Override
        public boolean providePreserveCase() {
            return preserveCase;
        }

        @Override
        public Pattern provideTokenRegexPattern() {
            return Pattern.compile(tokenRegexPattern);
        }

        @Override
        public boolean provideKeepSequenceBigrams() {
            return keepSequenceBigrams;
        }

        @Override
        public ImportFileSettings.DocumentIterationSchema provideIterationSchema() {
            return iterationSchema;
        }
    }

}
