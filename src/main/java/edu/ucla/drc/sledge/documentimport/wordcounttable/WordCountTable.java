package edu.ucla.drc.sledge.documentimport.wordcounttable;

import cc.mallet.types.Instance;
import cc.mallet.types.TokenSequence;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
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
            addStopwordItem.setOnAction(actionEvent -> {
                model.addStopword(row.getItem().getWord());
                tableView.getItems().remove(row.getItem());
            });

            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu)
            );
            return row;
        });
    }

    public void setData (ProjectModel model, ObjectProperty<Instance> selectedDocument) {
        this.model = model;
        selectedDocument.addListener((ObservableValue<? extends Instance> observable, Instance oldValue, Instance newValue) -> {
            TokenSequence ts = (TokenSequence)newValue.getData();
            WordCounter counter = new WordCounter(ts);
            List<WordCountEntry> wordCountEntries = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : counter.count().entrySet()) {
                wordCountEntries.add(new WordCountEntry(entry.getKey(), entry.getValue()));
            }
            setItems(FXCollections.observableArrayList(wordCountEntries));
            sort();
        });
    }

}
