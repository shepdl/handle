package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.Instance;
import cc.mallet.types.TokenSequence;
import edu.ucla.drc.sledge.documentimport.stopwords.TokenSequenceMarkStopwords;
import edu.ucla.drc.sledge.documents.Document;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCountTable extends TableView<WordCountEntry> {

    @FXML private TableColumn countColumn;
    private ProjectModel model;

    public WordCountTable () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WordCountTable.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setRowFactory((TableView<WordCountEntry> tableView) -> {
            final TableRow<WordCountEntry> row = new TableRow<>();
            final ContextMenu menu = new ContextMenu();
            final MenuItem addStopwordItem = new MenuItem("Add as stopword");
            menu.getItems().add(addStopwordItem);
            addStopwordItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    model.addStopword(row.getItem().getWord());
                    tableView.getItems().remove(row.getItem());
                }
            });

            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu)
            );
            return row;
        });
    }

    public void setData (ProjectModel model, ObjectProperty<Document> selectedDocument) {
        this.model = model;
        selectedDocument.addListener((ObservableValue<? extends Document> observable, Document oldValue, Document newValue) -> {
//            setItems(countWords(newValue.getContent());
            sort();
        });
    }

    private ObservableList<WordCountEntry> countWords (Instance instance) {
        Map<String, Integer> counts = new HashMap<>();

//        FeatureSequence fs = (FeatureSequence)instance.getData();
        TokenSequence fs = (TokenSequence)instance.getData();
        for (int i = 0; i < fs.size(); i++) {
//            String word = (String)fs.get(i);
            String word = fs.get(i).getText();
            if (fs.get(i).hasProperty(TokenSequenceMarkStopwords.IsStopword)) {
                continue;
            }
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
