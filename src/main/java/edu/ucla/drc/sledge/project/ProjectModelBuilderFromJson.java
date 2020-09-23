package edu.ucla.drc.sledge.project;

import cc.mallet.topics.TopicModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.documents.Document;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
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
        List<TopicModel> outTopicModels = new ArrayList<>();
        for (TopicModelImporter topicModelImporter : topicModels) {
            byte[] decodedTopicModel = Base64.getDecoder().decode(topicModelImporter.dehydratedTopicModel);
            ByteArrayInputStream bais = new ByteArrayInputStream(decodedTopicModel);
            try {
                ObjectInputStream ois = new ObjectInputStream(bais);
                TopicModel newTopicModel = new TopicModel(0);
                newTopicModel.readObject(ois);
                outTopicModels.add(newTopicModel);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                System.out.println("How are we missing a class?");
                ex.printStackTrace();
            }
        }

        return outTopicModels;
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

    private static class TopicModelImporter implements TopicModel.Importer {

        @JsonProperty("name") private String name;
        @JsonProperty("numTopics") private int numTopics;
        @JsonProperty("dehydratedTopicModel") private String dehydratedTopicModel;

        @Override
        public String name() {
            return null;
        }

        @Override
        public int numTopics() {
            return 0;
        }

        @Override
        public int topicMask() {
            return 0;
        }

        @Override
        public int topicBits() {
            return 0;
        }

        @Override
        public double[] alpha() {
            return new double[0];
        }

        @Override
        public double alphaSum() {
            return 0;
        }

        @Override
        public double beta() {
            return 0;
        }

        @Override
        public double betaSum() {
            return 0;
        }

        @Override
        public int[][] typeTopicCounts() {
            return new int[0][];
        }

        @Override
        public int[] tokensPerTopic() {
            return new int[0];
        }

        @Override
        public int[] docLengthCounts() {
            return new int[0];
        }

        @Override
        public int[][] topicDocCounts() {
            return new int[0][];
        }

        @Override
        public int numIterations() {
            return 0;
        }

        @Override
        public int burnInPeriod() {
            return 0;
        }

        @Override
        public int optimizeInterval() {
            return 0;
        }

        @Override
        public int randomSeed() {
            return 0;
        }
    }

    @JsonProperty("topicModels")
    private List<TopicModelImporter> topicModels;

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
