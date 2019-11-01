package edu.ucla.drc.sledge.documentimport;

import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.io.*;
import java.util.*;

public class StopwordsDialog {

    @FXML TableView<String> stopwordsTable;
    @FXML Button saveButton;
    @FXML Button cancelButton;
    @FXML ComboBox<StopwordSource> defaultStopwordsComboBox;

    Alert confirmAlert;

    private ProjectModel model;

    final ObservableList<String> stopwordsList = FXCollections.observableArrayList();

    @FXML
    private void initialize () {
        File stopwordsDirectory = new File(getClass().getResource("default-stopwords/").getFile());
        for (File file : stopwordsDirectory.listFiles()) {
            defaultStopwordsComboBox.getItems().add(new StopwordFile(file));
        }
    }

    @FXML
    private void addWordsFromFile (ActionEvent event) {
        StopwordSource sourceFile = defaultStopwordsComboBox.getValue();
        stopwordsList.addAll(sourceFile.provideWords());
    }

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
        Set<String> words = new HashSet<String>(stopwordsTable.getItems());
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

    @FXML
    private void clearStopwords(MouseEvent mouseEvent) {
        mouseEvent.consume();
        stopwordsList.clear();
        model.setStopwords(new HashSet<String>());
    }

    @FXML
    private void cancelEditing(MouseEvent mouseEvent) {
        mouseEvent.consume();
        Set<String> tableWords = new HashSet<String>(stopwordsTable.getItems());
        Set<String> projectWords = model.getStopwords();
        if (!tableWords.equals(projectWords)) {
            confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Stopwords have changed");
            confirmAlert.setHeaderText("Are you sure you want to discard your changes?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
        }
    }
}
