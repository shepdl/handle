package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCountTable extends TableView<WordCountEntry> {

    public WordCountTable () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WordCountTable.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData (ObjectProperty<Document> selectedDocument) {
        selectedDocument.addListener((ObservableValue<? extends Document> observable, Document oldValue, Document newValue) -> {
            setItems(countWords(newValue.getIngested()));
        });
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

}
