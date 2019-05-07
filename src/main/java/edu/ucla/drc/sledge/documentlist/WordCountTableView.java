package edu.ucla.drc.sledge.documentlist;

import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCountTableView {

    private ObjectProperty<Document> selectedDocument;

    public WordCountTableView(ObjectProperty<Document> selectedDocument) {

        this.selectedDocument = selectedDocument;
    }

    public void initialize (HBox root) {
        TableView<WordCountEntry> countsTable = new TableView<>();

        TableColumn wordColumn = new TableColumn("Word");
        wordColumn.setCellValueFactory(new PropertyValueFactory<WordCountEntry, String>("word"));
        TableColumn countColumn = new TableColumn("Count");
        countColumn.setCellValueFactory(new PropertyValueFactory<WordCountEntry, Integer>("count"));

        countsTable.getColumns().addAll(wordColumn, countColumn);

        selectedDocument.addListener(new ChangeListener<Document>() {
            @Override
            public void changed(ObservableValue<? extends Document> observable, Document oldValue, Document newValue) {
                countsTable.setItems(countWords(newValue.getIngested()));
            }
        });

        root.getChildren().add(countsTable);
    }

    private ObservableList<WordCountEntry> countWords (Instance instance) {
        Map<String, Integer> counts = new HashMap<>();

        FeatureSequence fs = (FeatureSequence)instance.getData();
        for (int i = 0; i < fs.getLength(); i++) {
            String word = (String)fs.get(i);
            if (!counts.containsKey(word)) {
                counts.put(word, 0);
            }
            counts.put(word, counts.get(word) + 1);
        }

        List<WordCountEntry> wordCountEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            wordCountEntries.add(new WordCountEntry(entry.getKey(), entry.getValue()));
        }

        return FXCollections.observableArrayList(wordCountEntries);
    }

    private ObservableList<WordCountEntry> countWords (String inText) {
        String[] words = inText.split("\\s+");
        Map<String, Integer> counts = new HashMap<>();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!counts.containsKey(word)) {
                counts.put(word, 0);
            }
            counts.put(word, counts.get(word) + 1);
        }
        List<WordCountEntry> wordCountEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            wordCountEntries.add(new WordCountEntry(entry.getKey(), entry.getValue()));
        }

        return FXCollections.observableArrayList(wordCountEntries);
    }

}
