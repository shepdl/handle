package edu.ucla.drc.sledge.project;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.documents.Document;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
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
    private List<DocumentExporter> documents;

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
            return new DocumentExporter(doc.getFile());
//            return new DocumentExporter(doc.getFile(), doc.getIngested());
        }).collect(Collectors.toList());
        this.importFileSettings = importFileSettings;
        this.stopwords = stopwords;
        this.topicModels = topicModels.stream().map((model) -> {
            return new TopicModel.TopicModelSettingsExporter(model);
        }).collect(Collectors.toList());
//        this.topicModels = new ArrayList<>(topicModels);
    }

    public void writeToFile (File outFile) {
        StringWriter writer = new StringWriter();
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        try {
            XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(writer);
            XmlMapper mapper = new XmlMapper();
            sw.writeStartDocument();
            sw.writeStartElement("root");
            mapper.writeValue(sw, this);
            sw.writeEndElement();
            sw.writeEndDocument();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(writer.toString());
    }

    public final void writeObject (ObjectOutputStream out) throws IOException {
        out.writeObject(name);
        out.writeObject(documents);
        out.writeObject(importFileSettings);
        out.writeObject(instances);
        out.writeObject(stopwords);
        out.writeObject(topicModels);
    }

    public final void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
        int version = in.readInt();
        this.name = (String)in.readObject();
        this.documents = (List<DocumentExporter>)in.readObject();
        this.importFileSettings = (ImportFileSettings)in.readObject();
        this.instances = (InstanceList)in.readObject();
        this.stopwords = (Set<String>)in.readObject();
//        this.topicModels = (ArrayList<TopicModel>)in.readObject();
    }

    private static class DocumentExporter implements Serializable {
        private static final long SerialVersionUID = 1;

        @JsonProperty("file")
        private String fileContent;

        public DocumentExporter () {

        }

        public DocumentExporter (File file) {
            StringBuilder builder = new StringBuilder();
            try (Stream<String> stream = Files.lines(file.toPath())) {
                fileContent = stream.collect(Collectors.joining("\n"));
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    public ProjectModel toModel () {
        ProjectModel model = new ProjectModel();
        model.setStopwords(stopwords);
        return model;
    }

    public static class ProjectModelBuilderToJson implements ProjectModel.Exporter {

        @JsonProperty("name")
        private String name;

        @JsonProperty("documents")
        private List<DocumentExporter> documents;

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

        @JsonProperty("instances")
        private InstanceList instances;

        @JsonProperty("stopwords")
        private Set<String> stopwords;

        @JsonProperty("topicModels")
        private List<TopicModel.TopicModelSettingsExporter> topicModels;

        @Override
        public void addDocuments(List<Document> documents) {
            this.documents = documents.stream().map((doc) -> {
                return new DocumentExporter(doc.getFile());
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

        }
    }

}
