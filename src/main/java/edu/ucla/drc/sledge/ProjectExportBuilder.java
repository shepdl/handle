package edu.ucla.drc.sledge;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.beans.property.ObjectProperty;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private List<TopicModel> topicModels;

    public ProjectExportBuilder(String name, List<Document>documents,
            ImportFileSettings importFileSettings, InstanceList instances, Set<String> stopwords,
            List<TopicModel> topicModels
    ) {
        this.name = name;
//        this.documents = documents;
        this.documents = documents.stream().map((doc) -> {
            return new DocumentExporter(doc.getFile(), doc.getIngested());
        }).collect(Collectors.toList());
        this.importFileSettings = importFileSettings;
        this.instances = instances;
        this.stopwords = stopwords;
        this.topicModels = new ArrayList<>(topicModels);
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
        this.topicModels = (ArrayList<TopicModel>)in.readObject();
    }

    private static class DocumentExporter implements Serializable {
        private static final long SerialVersionUID = 1;

        private final File file;
        private final Instance instance;

        public DocumentExporter (File file, Instance instance) {
            this.file = file;
            this.instance = instance;
        }
    }

}
