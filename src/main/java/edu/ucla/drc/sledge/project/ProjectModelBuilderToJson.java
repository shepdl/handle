package edu.ucla.drc.sledge.project;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.util.PropertyList;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.documents.Document;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProjectModelBuilderToJson implements ProjectModel.Exporter {

    @JsonProperty("name")
    private String name;

    @JsonProperty("documents")
    private List<Document.Exporter> documents;

    private static class ImportSettingsExporter implements ImportFileSettings.Exporter {

        @JsonProperty("preserveCase")
        private boolean preserveCase;

        @JsonProperty("tokenRegex")
        private String pattern;

        @JsonProperty("keepSequence")
        private boolean keepSequence;

        @JsonProperty("iterationSchema")
        private ImportFileSettings.DocumentIterationSchema schema;

        @Override
        public void addPreserveCase(boolean preserveCase) {
            this.preserveCase = preserveCase;
        }

        @Override
        public void addTokenRegexPattern(Pattern pattern) {
            this.pattern = pattern.pattern();
        }

        @Override
        public void addKeepSequenceBigrams(boolean keepSequence) {
            this.keepSequence = keepSequence;
        }

        @Override
        public void addDocumentIterationSchema(ImportFileSettings.DocumentIterationSchema schema) {
            this.schema = schema;
        }
    }

    @JsonProperty("importFileSettings")
    private ImportSettingsExporter importFileSettings;

    @JsonProperty("stopwords")
    private Set<String> stopwords;

    @JsonProperty("topicModels")
    private List<TopicModel.Exporter> topicModels;

    @Override
    public void addDocuments(List<Document> documents) {
        this.documents = documents.stream().map((doc) -> {
            DocExporterToJson exporter = new DocExporterToJson();
            try {
                doc.exportTo(exporter);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return exporter;
//                return new DocumentExporter(doc.getFile());
        }).collect(Collectors.toList());
    }

    @Override
    public void addSettings(ImportFileSettings settings) {
        ImportSettingsExporter builder = new ImportSettingsExporter();
        settings.exportTo(builder);
        importFileSettings = builder;
    }

    @Override
    public void addStopwords(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    @Override
    public void addTopicModels(List<TopicModel> topicModels) {
        this.topicModels = topicModels.stream().map((model) -> {
            TopicModelExporterToJson exporter = new TopicModelExporterToJson();
            model.exportTo(exporter);
            return exporter;
        }).collect(Collectors.toList());

    }

    private static class DocExporterToJson implements Document.Exporter {

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

    private static class TopicModelExporterToJson implements TopicModel.Exporter {

        @JsonProperty("name")
        private String name;
        @JsonProperty("numTopics")
        private int numTopics;
//        @JsonProperty("alphabet")
        private List<String> alphabet;
//        @JsonProperty("labelAlphabet")
        private List<String> labelAlphabet;
//        @JsonProperty("topicMask")
        private int topicMask;
//        @JsonProperty("topicBits")
        private int topicBits;
//        @JsonProperty("alpha")
        private double[] alpha;
//        @JsonProperty("alphaSum")
        private double alphaSum;
//        @JsonProperty("beta")
        private double beta;
//        @JsonProperty("betaSum")
        private double betaSum;
//        @JsonProperty("typeTopicCounts")
        private int[][] typeTopicCounts;
//        @JsonProperty("tokensPerTopic")
        private int[] tokensPerTopic;
//        @JsonProperty("docLengthCounts")
        private int[] docLengthCounts;
//        @JsonProperty("topicDocCounts")
        private int[][] topicDocCounts;
//        @JsonProperty("numIterations")
        private int numIterations;
//        @JsonProperty("burnInPeriod")
        private int burnInPeriod;
//        @JsonProperty("optimizeInterval")
        private int optimizeInterval;
//        @JsonProperty("randomSeed")
        private int randomSeed;

        @JsonProperty("dehydratedTopicModel")
        private String dehydratedTopicModel;

        @Override
        public void addName(String name) {
            this.name = name;
        }

        @Override
        public void addNumTopics(int numTopics) {
            this.numTopics = numTopics;
        }

        @Override
        public void addAlphabet(Alphabet alphabet) {
            this.alphabet = new ArrayList<>();
            if (alphabet != null) {
                Iterator<String> alphabetIterator = (Iterator<String>)alphabet.iterator();
                while (alphabetIterator.hasNext()) {
                    this.alphabet.add(alphabetIterator.next());
                }
            }
        }

        @Override
        public void addLabelAlphabet(LabelAlphabet alphabet) {
            this.labelAlphabet = new ArrayList<>();
            if (alphabet != null) {
                Iterator<String> alphabetIterator = (Iterator<String>)alphabet.iterator();
                while (alphabetIterator.hasNext()) {
                    this.alphabet.add(alphabetIterator.next());
                }
            }
        }

        @Override
        public void addTopicMask(int topicMask) {
            this.topicMask = topicMask;
        }

        @Override
        public void addTopicBits(int topicBits) {
            this.topicBits = topicBits;
        }

        @Override
        public void addAlpha(double[] alpha) {
            this.alpha = alpha;
        }

        @Override
        public void addAlphaSum(double alphaSum) {
            this.alphaSum = alphaSum;
        }

        @Override
        public void addBeta(double beta) {
            this.beta = beta;
        }

        @Override
        public void addBetaSum(double betaSum) {
            this.betaSum = betaSum;
        }

        @Override
        public void addTypeTopicCounts(int[][] typeTopicCounts) {
            this.typeTopicCounts = typeTopicCounts;
        }

        @Override
        public void addTokensPerTopic(int[] tokensPerTopic) {
            this.tokensPerTopic = tokensPerTopic;
        }

        @Override
        public void addDocLengthCounts(int[] docLengthCounts) {
            this.docLengthCounts = docLengthCounts;
        }

        @Override
        public void addTopicDocCounts(int[][] topicDocCounts) {
            this.topicDocCounts = topicDocCounts;
        }

        @Override
        public void addNumIterations(int numIterations) {
            this.numIterations = numIterations;
        }

        @Override
        public void addBurnInPeriod(int burnInPeriod) {
            this.burnInPeriod = burnInPeriod;
        }

        @Override
        public void addOptimizeInterval(int optimizeInterval) {
            this.optimizeInterval = optimizeInterval;
        }

        @Override
        public void addRandomSeed(int randomSeed) {
            this.randomSeed = randomSeed;
        }

        @Override
        public void addSerializedTopicModel(TopicModel topicModel) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            topicModel.writeObject(oos);
            dehydratedTopicModel = Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }
}
