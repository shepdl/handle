package edu.ucla.drc.sledge.documentimport;

import edu.ucla.drc.sledge.ProjectModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class StopwordsDialog {

    @FXML
    TableView stopwordsTable;
    @FXML
    Button saveButton;
    @FXML
    Button cancelButton;


    private ProjectModel model;

    final ObservableList<String> stopwordsList = FXCollections.observableArrayList();

    public void setModel (ProjectModel model) {
        this.model = model;
        TableColumn<String, String> wordColumn = new TableColumn("Word");
        stopwordsTable.getColumns().add(0, wordColumn);
        wordColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        for (String word : model.getStopwords()) {
            stopwordsList.add(word);
        }

        stopwordsTable.setItems(stopwordsList);
    }

    public void saveStopwords(MouseEvent event) {
        Set<String> words = new HashSet<>(stopwordsTable.getItems());
        model.setStopwords(words);
    }

    public void setTransferMode(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    public void dropFile(DragEvent event) {
        Dragboard db = event.getDragboard();
        Set<String> words = new HashSet<>(stopwordsTable.getItems());
        for (File file : db.getFiles()) {
            try {
                Scanner lineScanner = new Scanner(file);
                while (lineScanner.hasNextLine()) {
                    Scanner wordScanner = new Scanner(lineScanner.nextLine());
                    while (wordScanner.hasNext()) {
                        words.add(wordScanner.next());
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        List<String> wordsList = new ArrayList<>(words);
        Collections.sort(wordsList);
        stopwordsList.removeAll();
        stopwordsList.addAll(wordsList);
        event.setDropCompleted(true);
        event.consume();
    }
}
